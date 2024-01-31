"use client";
import {AppShell, Button, Card, Center, Divider, Group, Image, Space, Stack, Text, Title} from '@mantine/core';

import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";
import React, {useEffect, useState} from "react";
import {useAuthContext} from "@/app/context/AuthContext";
import {ExtendedForecastResponse, NavigationProps} from "@/app/util/types";
import {axiosInstance} from "@/app/util/axiosInstance";
import {useUserReportContext} from "@/app/context/UserReportContext";
import '@mantine/carousel/styles.css';
import {Carousel} from '@mantine/carousel';
import {IconArrowLeft} from '@tabler/icons-react';
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function UserForecastPage({onNavigate}: NavigationProps) {

    const auth = useAuthContext();
    const report = useUserReportContext();
    const fav = useFavoritesContext();

    const [reportData, setReportData] = useState<ExtendedForecastResponse | ExtendedForecastResponse[]>([]);

    useEffect(() => {
        async function fetchResponses() {
            try {
                let peaks = report.peakIds;
                const responses = await Promise.all(
                    peaks.map(peak => {
                        const url = `/report/extended/${peak}`;
                        return axiosInstance.get<ExtendedForecastResponse>(url)
                            .then(res => res.data)
                            .catch(err => console.error(err));
                    })
                );
                // Filter out undefined or null responses if any requests fail
                const validResponses = responses.filter((response): response is ExtendedForecastResponse => response !== undefined);
                setReportData(validResponses);
            } catch (error) {
                console.error("Error fetching data", error);
            }
        }

        fetchResponses();
    }, [report, setReportData]);


    useEffect(() => {
        if (report.reportData) {
            setReportData(report.reportData);
        }
    }, [report, setReportData]);
    useEffect(() => console.log("report data is :" + JSON.stringify(reportData)), [reportData]);
    const renderForecast = (dayForecast) => {
        console.log(JSON.stringify(dayForecast));
        const AM = dayForecast.forecastData.AM;
        const PM = dayForecast.forecastData.PM;
        const Night = dayForecast.forecastData.Night;
        return (
            <Card>
                <Center>
                    <Stack>
                        <Center>
                            <Stack>
                                <Title order={1}>{AM.peakName}</Title>
                                <Divider/>
                            </Stack>
                        </Center>
                        <Center>
                            <Stack>
                                <Title order={2}>{AM.dayOfTheWeek}</Title>
                                <Divider/>
                            </Stack>
                        </Center>

                        <Group>
                            <Stack>
                                <Title order={2}>Morning</Title>
                                <Group>
                                    <Text>High: {AM.high}°</Text>
                                    <Text>Low: {AM.low}°</Text>
                                </Group>
                                <Text>Conditions: {AM.weatherConditions} </Text>
                                <Text>Wind: {AM.windConditions}</Text>
                                <Group>
                                    <Text>Rain: {AM.expectedRainfall}</Text>
                                    <Text>Snow: {AM.expectedSnowfall}</Text>
                                </Group>
                            </Stack>
                            <Space h="xl"/>

                            <Stack>
                                <Title order={2}>Evening</Title>
                                <Group>
                                    <Text>High: {PM.high}°</Text>
                                    <Text>Low: {PM.low}°</Text>
                                </Group>
                                <Text>Conditions: {PM.weatherConditions} </Text>
                                <Text>Wind: {PM.windConditions}</Text>
                                <Group>
                                    <Text>Rain: {PM.expectedRainfall}</Text>
                                    <Text>Snow: {PM.expectedSnowfall}</Text>
                                </Group>
                            </Stack>
                            <Space h="xl"/>

                            <Stack>
                                <Title order={2}>Night</Title>
                                <Group>
                                    <Text>High: {Night.high}°</Text>
                                    <Text>Low: {Night.low}°</Text>
                                </Group>
                                <Text>Conditions: {Night.weatherConditions} </Text>
                                <Text>Wind: {Night.windConditions}</Text>
                                <Group>
                                    <Text>Rain: {Night.expectedRainfall}</Text>
                                    <Text>Snow: {Night.expectedSnowfall}</Text>
                                </Group>
                            </Stack>
                        </Group>
                        <Space h="xl"/>
                    </Stack>
                </Center>
            </Card>
        );
    };
    const buildCarousel = (peakForecasts, peakName) => {
        console.log("peakForecasts : " + JSON.stringify(peakForecasts));
        return (
            <div key={peakName}>

                <Space h="lg"/>
                <Carousel
                    align="start"
                    controlsOffset="xs"
                    style={{height: 500}}
                    withIndicators={true}>
                    {peakForecasts.map((dayForecast, index) => (
                        <Carousel.Slide key={index}>
                            {renderForecast(dayForecast)}
                        </Carousel.Slide>
                    ))}
                </Carousel>
                <Divider/>

            </div>
        );
    };

    return (<>
            <AppShell
                header={{height: 70}}
                padding="md">
                <AppShell.Header>
                    {auth.user ? <AuthenticatedNavBar onNavigate={onNavigate} onHomeClick={fav.fetchFavorites}/> :
                        <DefaultNavBar onNavigate={onNavigate}/>}
                </AppShell.Header>
                <AppShell.Main>
                    <Image radius="md"
                           src={"/mountainCloudy.jpg"}
                           h={200}
                           fallbackSrc="https://placehold.co/600x400?text=Placeholder"/>
                    <Group justify={"space-between"}>
                        <div style={{width: "10vw"}}>
                            <Button onClick={() => {
                                onNavigate("favorites")
                            }} leftSection={<IconArrowLeft/>}>Back to favorites</Button>
                        </div>
                        <div style={{width: "70vw"}}>
                            <Center>
                                <Title>
                                    View selected forecasts
                                </Title>
                            </Center>
                        </div>
                        <div style={{width: "10vw"}}></div>
                    </Group>

                    {reportData && reportData.length > 0 ? (
                        <Center>
                            <Stack>
                                {reportData && reportData.length > 0 && reportData.map(peakDataArray => {
                                    const peakName = peakDataArray[0].forecastData.AM.peakName;
                                    return buildCarousel(peakDataArray, peakName);
                                })}
                            </Stack>
                        </Center>
                    ) : (<>
                        <Text>No peaks selected. Please choose at least one entry from the favorites list to view the
                            forecasts.</Text>
                    </>)}

                </AppShell.Main>
            </AppShell>
        </>
    )
}