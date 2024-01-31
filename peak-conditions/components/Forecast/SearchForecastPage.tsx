"use client";
import {
    AppShell,
    Button,
    Card,
    Center,
    Divider,
    Group,
    Menu,
    Space,
    Stack,
    Text,
    Title,
    useMantineTheme
} from '@mantine/core';

import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";
import React, {useEffect, useState} from "react";
import {useAuthContext} from "@/app/context/AuthContext";
import {useForecastContext} from "@/app/context/ForecastContext";
import {DailyForecastResponse, NavigationProps} from "@/app/util/types";
import '@mantine/carousel/styles.css';
import {Carousel} from '@mantine/carousel';
import {axiosInstance} from "@/app/util/axiosInstance";
import {IconArrowLeft} from "@tabler/icons-react";
import classes = Menu.classes;
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function SearchForecastPage({onNavigate}: NavigationProps) {
    const theme = useMantineTheme();
    const auth = useAuthContext();
    const fav = useFavoritesContext();
    const forecast = useForecastContext();
    const [pageTitle, setPageTitle] = useState("Loading...");
    const [btnDisabled, setBtnDisabled] = useState(false);
    const [report, setReport] = useState<DailyForecastResponse[]>([]);
    const reportArray = Array.isArray(report) ? report : [report];
    useEffect(() => {
        if (Array.isArray(report) && report.length > 0) {
            const section = report[report.length - 1]; // Access the last element without removing it

            if (section?.forecastData) {
                const peakName = section.forecastData.AM?.peakName || "Loading...";
                setPageTitle(peakName);
            } else {
                setPageTitle("Loading...");
            }
        } else {
            setPageTitle("Loading...");
        }
    }, [report]);
    useEffect(() => {
        if (forecast.forecast) {
            console.log('forecast data present', forecast.forecast);
            const normalizedReport = Array.isArray(forecast.forecast) ? forecast.forecast : [forecast.forecast];
            setReport(normalizedReport);
        }
    }, [forecast.forecast, setReport]);
    const handleBackClick = () => {
        onNavigate('searchResults')
    }
    const savePeakToFavs = async () => {
        const peakId = forecast.peakId;
        let dto = {
            "profileId": auth.user?.profile.profileId,
            "peakId": peakId
        }
        await axiosInstance.put("/profile/peaks", dto).then((res) => {
            console.log('server response is:' + JSON.stringify(res.data));
            auth.setProfile(res.data);
            setBtnDisabled(true);
        }).catch((err) => console.log(err));
        console.log("auth profile is now :" + JSON.stringify(auth.profile));
    }
    const carouselStyles = {
        width: "60vw",
        indicator: {
            width: 8,
            height: 8,
            backgroundColor: theme.colors.blue[1], // Accessing blue1 color from the theme
            '&:hover': {
                backgroundColor: theme.colors.blue[2],
            },
            '&[data-active]': {
                backgroundColor: theme.colors.blue[3],
            },
        },
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
                    <Group justify={"space-between"}>
                        <div style={{width: "30vw"}}>
                            <Button onClick={() => {
                                onNavigate('searchResults')
                            }} leftSection={<IconArrowLeft/>}>
                                search results
                            </Button>
                        </div>
                        <div style={{width: "30vw"}}>
                            <Center>
                                {auth.user && report ? (<>
                                    <Stack>
                                        <Title order={1}>{pageTitle}</Title>
                                        <Button onClick={() => {
                                            savePeakToFavs();
                                        }} disabled={btnDisabled}>Save to Favorite Peaks</Button>
                                    </Stack>
                                </>) : (<>
                                    <Title order={1}>{pageTitle}</Title>
                                </>)}
                            </Center>
                        </div>
                        <div style={{width: "30vw"}}></div>

                    </Group>
                    <Space h="xl"/>
                    <Divider/>
                    <Space h="xl"/>
                    <Center>
                        <Carousel
                            align="start"
                            controlsOffset="xs"
                            classNames={{indicator: classes.indicator}}
                            style={carouselStyles}
                            withIndicators={true}>
                            {reportArray.map((item: DailyForecastResponse, index: number) => {
                                if (!item || !item.forecastData) {
                                    return <div key={index}>Loading forecast data...</div>;
                                } return(
                                    <Carousel.Slide key={item.forecastData.AM.dayOfTheWeek}>
                                        <Card>
                                            <Center>
                                                <Stack>
                                                    <Title order={1}>{item.forecastData.AM.dayOfTheWeek}</Title>
                                                    <Divider/>
                                                </Stack>
                                            </Center>
                                            <Center>
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
                                                        <Space h="xl"/>
                                                    </Stack>
                                                    <Space h="xl"/>
                                                    <Space h="xl"/>
                                                    <Stack>
                                                        <Title order={2}>Afternoon</Title>
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
                                                        <Space h="xl"/>
                                                    </Stack>
                                                    <Space h="xl"/>
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
                                                        <Space h="xl"/>
                                                    </Stack>
                                                </Group>
                                            </Center>
                                        </Card>
                                    </Carousel.Slide>)}
                            )}
                        </Carousel>
                    </Center>
                    <Space h="xl"/>
                    <Space h="xl"/>
                    <Space h="xl"/>
                    {!auth.user ? (
                        <Center>
                            <Stack>
                                <Center>
                                    <Title>Want to see the extended forecast?</Title>
                                </Center>
                                <Center>
                                    <Title order={3}>Get access to 6-Day forecasts when you sign up for an
                                        account!</Title>
                                </Center>
                                <Button onClick={() => {
                                    onNavigate('register')
                                }}>
                                    Sign up for an account today!
                                </Button>
                            </Stack>
                        </Center>) : (<></>)}
                </AppShell.Main>
            </AppShell>
        </>
    )
}