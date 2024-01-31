'use client';
import {AppShell, Button, Center, Divider, Group, Image, Space, Stack, Text, Title} from "@mantine/core";
import React, {useEffect, useState} from "react";
import {useAuthContext} from "@/app/context/AuthContext";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";

import '@mantine/carousel/styles.css';
import DefaultDemo from "@/components/Demo/DefaultDemo";
import SearchBar from "@/components/Search/SearchBar";
import {NavigationProps} from "@/app/util/types";

export default function DefaultHomePageContent({onNavigate}: NavigationProps) {

    const auth = useAuthContext();
    const [demoActive, setDemoActive] = useState(false);
    const [buttonVisible, setButtonVisible] = useState(true);
    const checkForLogin = () => {
        if (auth.user) {
            onNavigate('authenticatedHome');
        }
    }
    useEffect(() => {
        checkForLogin();
    }, [checkForLogin]);

    const handleBtnClick = () => {
        setButtonVisible(false);
        setDemoActive(true);
    }

    return (<>{checkForLogin && (<>
            <AppShell
                header={{height: 70}}
                padding="md">
                <AppShell.Header>
                    <DefaultNavBar onNavigate={onNavigate}/>
                </AppShell.Header>
                <AppShell.Main>

                    <Center>

                        <Stack align={"flex-start"}>
                            <Title order={1}>Don't get caught in poor conditions!</Title>
                            <Title order={4}>Traditional weather apps only track the forecast for nearby towns.</Title>
                            <Text style={{textAlign: "center", margin: "auto", width: "30vw"}}>
                                Peak Conditions is a different kind of weather app. Made for adventures,
                                this app is designed to track the weather conditions at higher elevations
                                where the average weather app fails. Stay safe in the back country by knowing
                                exactly what weather you will encounter on your trip.
                            </Text>
                            <Image radius="md"
                                   src={"/mountainDusk.jpg"}
                                   h={200}
                                   fallbackSrc="https://placehold.co/600x400?text=Placeholder"/>
                        </Stack>
                    </Center>

                    <Space h={"lg"}/>
                    <Center>
                        <Title>View the daily forecast of popular mountain tops</Title>
                    </Center>
                    <Space h={"lg"}/>
                    <Divider/>
                    <Space h={"lg"}/>
                    <Center>
                        {buttonVisible && (
                            <>
                                <Button onClick={() => handleBtnClick()}>
                                    Click to see the demo
                                </Button>
                                <Space h={"xl"}/>
                            </>)}
                        {demoActive && (
                            <>
                                <Space/>
                                <DefaultDemo/>
                            </>
                        )}
                    </Center>
                    <Center>
                        <Group>
                            <Center>
                                <Title order={1}>Find your happy place!</Title>
                            </Center>
                            <Center>
                                <SearchBar onNavigate={onNavigate}/>
                            </Center>
                        </Group>
                    </Center>
                    <Center>
                        <Title order={4}>Search from over 10,000 mountains from across the globe.</Title>
                    </Center>
                    <Center>
                        <Text style={{textAlign: "center", width: "50vw"}}>
                            We collect data for most mountain ranges across the world.
                            Search mountain peaks by name, or by the name of the mountain range.
                            See the current daily forecast for any mountain, or browse the peaks that make up your
                            favorite range.
                        </Text>
                    </Center>
                </AppShell.Main>
            </AppShell></>)}</>
    );
}