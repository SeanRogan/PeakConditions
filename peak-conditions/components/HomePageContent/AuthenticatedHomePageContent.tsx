'use client';
import {useAuthContext} from "@/app/context/AuthContext";
import {AppShell, Card, Center, Divider, Group, Image, Space, Stack, Text, Title} from '@mantine/core';
import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import React, {useEffect} from "react";
import {NavigationProps} from "@/app/util/types";
import {Carousel} from "@mantine/carousel";
import SearchBar from "@/components/Search/SearchBar";
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function AuthenticatedHomePageContent({onNavigate}: NavigationProps) {
    const auth = useAuthContext();
    const favctx = useFavoritesContext();

    useEffect(() => {
        favctx.fetchFavorites().then(() => {
            console.log("fetchFavorites complete :" + JSON.stringify(favctx.groupedReports));
        });
    }, [auth.profile?.favoritePeaks])

    const buildCarousel = (peakReports, peakName) => (
        <div key={peakName}>
            <Carousel
                align="start"
                controlsOffset="xs"
                withIndicators={true}>
                {peakReports.map((report, index) => (
                    <Carousel.Slide key={index}>
                        {slides(report)}
                    </Carousel.Slide>
                ))}
            </Carousel>
            <Space h="xl"/>
        </div>
    );

    const slides = (report) => {
        const reports = Array.isArray(report) ? report : [report];
        if (!Array.isArray(reports)) {
            console.error("Expected an array, got:", report);
            return null; // or return an empty array or any other fallback
        } else {
            //console.log(JSON.stringify(report));
        }
        return reports.map((item, index) => {
                return (
                    <Carousel.Slide key={index}>
                        <Center>
                            <Title order={1}>{item.forecastData.AM.peakName}</Title>
                        </Center>
                        <Card>
                            <Center>
                                <Stack>
                                    <Center>
                                        <Stack>
                                            <Title order={2}>{item.forecastData.AM.dayOfTheWeek}</Title>
                                            <Divider/>
                                        </Stack>
                                    </Center>
                                    <Group>
                                        <Stack>
                                            <Title order={2}>Morning</Title>
                                            <Group>
                                                <Text>High: {item.forecastData.AM.high}°</Text>
                                                <Text>Low: {item.forecastData.AM.low}°</Text>
                                            </Group>
                                            <Text>Conditions: {item.forecastData.AM.weatherConditions} </Text>
                                            <Text>Wind: {item.forecastData.AM.windConditions}</Text>
                                            <Group>
                                                <Text>Rain: {item.forecastData.AM.expectedRainfall}</Text>
                                                <Text>Snow: {item.forecastData.AM.expectedSnowfall}</Text>
                                            </Group>
                                        </Stack>
                                        <Space h="xl"/>

                                        <Stack>
                                            <Title order={2}>Evening</Title>
                                            <Group>
                                                <Text>High: {item.forecastData.PM.high}°</Text>
                                                <Text>Low: {item.forecastData.PM.low}°</Text>
                                            </Group>
                                            <Text>Conditions: {item.forecastData.PM.weatherConditions} </Text>
                                            <Text>Wind: {item.forecastData.PM.windConditions}</Text>
                                            <Group>
                                                <Text>Rain: {item.forecastData.PM.expectedRainfall}</Text>
                                                <Text>Snow: {item.forecastData.PM.expectedSnowfall}</Text>
                                            </Group>
                                        </Stack>
                                        <Space h="xl"/>

                                        <Stack>
                                            <Title order={2}>Night</Title>
                                            <Group>
                                                <Text>High: {item.forecastData.Night.high}°</Text>
                                                <Text>Low: {item.forecastData.Night.low}°</Text>
                                            </Group>
                                            <Text>Conditions: {item.forecastData.Night.weatherConditions} </Text>
                                            <Text>Wind: {item.forecastData.Night.windConditions}</Text>
                                            <Group>
                                                <Text>Rain: {item.forecastData.Night.expectedRainfall}</Text>
                                                <Text>Snow: {item.forecastData.Night.expectedSnowfall}</Text>
                                            </Group>
                                        </Stack>
                                    </Group>
                                    <Space h="xl"/>
                                </Stack>
                            </Center>
                        </Card>
                    </Carousel.Slide>
                );
            }
        );
    };
    return (
        <>
            <AppShell
                header={{height: 70}}
                padding="md">
                <AppShell.Header>
                    <AuthenticatedNavBar onNavigate={onNavigate} onHomeClick={favctx.fetchFavorites}/>
                </AppShell.Header>
                <AppShell.Main>
                    {auth.user && (
                        <>
                            <Center>
                                <Stack style={{width: "50vw"}}>
                                    <Image radius="md"
                                           src={"/mountainCloudy.jpg"}
                                           h={200}
                                           fallbackSrc="https://placehold.co/600x400?text=Placeholder"/>
                                    {auth.profile?.favoritePeaks?.length === 0 ? (
                                        <>
                                            <Title style={{margin: "auto", textAlign: "center"}}>
                                                Your favorites list is empty
                                            </Title>
                                            <Center>
                                                <Stack>
                                                    <Title order={3} style={{margin: "auto", textAlign: "center"}}>
                                                        Search for mountains to add to your favorites list
                                                    </Title>
                                                    <Center>
                                                        <SearchBar onNavigate={onNavigate}/>
                                                    </Center>
                                                </Stack>
                                            </Center>
                                        </>
                                    ) : (
                                        <>
                                            <Center>
                                                <Title order={1} style={{margin: "auto", textAlign: "center"}}>
                                                    See which
                                                    of your
                                                    favorite mountains are experiencing Peak Conditions!
                                                </Title>
                                            </Center>
                                            <Center>
                                                <Title order={3}
                                                       style={{width: "45vw", margin: "auto", textAlign: "center"}}>
                                                    Scroll
                                                    down to check out the forecasts of your favorite peaks, filtered by
                                                    your
                                                    weather preferences.
                                                </Title>
                                            </Center>
                                        </>
                                    )}
                                </Stack>
                            </Center>
                            <Space h={"xl"}/>

                            {Object.keys(favctx.groupedReports).length > 0 ? (
                                Object.entries(favctx.groupedReports).map(([peakName, peakReports]) =>
                                    buildCarousel(peakReports, peakName)
                                )
                            ) : (
                                <>
                                    <Title order={3} style={{margin: "auto", textAlign: "center"}}>
                                        There are no mountains in your favorites list experiencing peak conditions at
                                        this time.
                                    </Title>
                                </>
                            )}
                        </>
                    )}
                </AppShell.Main>
            </AppShell></>);
}