'use client';
import {Card, Center, Divider, Group, Space, Stack, Text, Title} from '@mantine/core';
import '@mantine/carousel/styles.css';
import {Carousel} from '@mantine/carousel';
import {axiosInstance} from "@/app/util/axiosInstance";
import React, {useEffect, useState} from "react";

export default function DefaultDemo() {
    const [data, setData] = useState([]);

    useEffect(() => {
        async function fetchDemoResponses() {
            try {
                let demoPeaks = [1625, 2614, 2506, 4806, 6489, 6708, 6151, 6716, 7580, 7584, 9300, 6700];
                const responses = await Promise.all(
                    demoPeaks.map(peak => {
                        const url = `/public/report/daily/${peak}`;
                        return axiosInstance.get(url)
                            .then(res => res.data)
                            .catch(err => console.error(err));
                    })
                );

                // Filter out undefined or null responses
                setData(responses.filter(response => response !== null));
            } catch (error) {
                console.error("Error fetching data", error);
            }
        }

        fetchDemoResponses();
    }, [setData]);

    const slides = data.map((item, index) => {

        return (
            <Carousel.Slide key={index}>
                <Center>
                    <Title order={1}>{item[0].forecastData.AM.peakName}</Title>
                </Center>
                <Card>
                    <Center>
                        <Stack>
                            <Center>
                                <Stack>
                                    <Title order={2}>{item[0].forecastData.AM.dayOfTheWeek}</Title>
                                    <Divider/>
                                </Stack>
                            </Center>

                            <Group>
                                <Stack>
                                    <Title order={2}>Morning</Title>
                                    <Group>
                                        <Text>High: {item[0].forecastData.AM.high}°</Text>
                                        <Text>Low: {item[0].forecastData.AM.low}°</Text>
                                    </Group>
                                    <Text>Conditions: {item[0].forecastData.AM.weatherConditions} </Text>
                                    <Text>Wind: {item[0].forecastData.AM.windConditions}</Text>
                                    <Group>
                                        <Text>Rain: {item[0].forecastData.AM.expectedRainfall}</Text>
                                        <Text>Snow: {item[0].forecastData.AM.expectedSnowfall}</Text>
                                    </Group>
                                </Stack>
                                <Space h="xl"/>

                                <Stack>
                                    <Title order={2}>Evening</Title>
                                    <Group>
                                        <Text>High: {item[0].forecastData.PM.high}°</Text>
                                        <Text>Low: {item[0].forecastData.PM.low}°</Text>
                                    </Group>
                                    <Text>Conditions: {item[0].forecastData.PM.weatherConditions} </Text>
                                    <Text>Wind: {item[0].forecastData.PM.windConditions}</Text>
                                    <Group>
                                        <Text>Rain: {item[0].forecastData.PM.expectedRainfall}</Text>
                                        <Text>Snow: {item[0].forecastData.PM.expectedSnowfall}</Text>
                                    </Group>
                                </Stack>
                                <Space h="xl"/>

                                <Stack>
                                    <Title order={2}>Night</Title>
                                    <Group>
                                        <Text>High: {item[0].forecastData.Night.high}°</Text>
                                        <Text>Low: {item[0].forecastData.Night.low}°</Text>
                                    </Group>
                                    <Text>Conditions: {item[0].forecastData.Night.weatherConditions} </Text>
                                    <Text>Wind: {item[0].forecastData.Night.windConditions}</Text>
                                    <Group>
                                        <Text>Rain: {item[0].forecastData.Night.expectedRainfall}</Text>
                                        <Text>Snow: {item[0].forecastData.Night.expectedSnowfall}</Text>
                                    </Group>
                                </Stack>
                            </Group>
                            <Space h="xl"/>
                        </Stack>
                    </Center>
                </Card>
            </Carousel.Slide>
        );
    });

    return (
        <>
            {data ? (
                <Carousel
                    align="start"
                    controlsOffset="xs"
                    withIndicators={true}
                    style={{width: "95vw"}}>
                    {slides}
                </Carousel>
            ) : (<></>)}
        </>
    );
}