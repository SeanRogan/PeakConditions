import {AppShell, Center, Image, Space, Stack, Text, Title} from "@mantine/core";
import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import React from "react";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";
import {useAuthContext} from "@/app/context/AuthContext";
import {NavbarProps} from "@/app/util/types";
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function AboutPage({onNavigate}) {
    const auth = useAuthContext();
    const fav = useFavoritesContext();
    return (
        <>
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
                    <Center>
                        <Title>About Peak Conditions</Title>
                    </Center>
                    <Space h={"lg"}/>
                    <Center>
                        <Text style={{width: "80vw", margin: "auto", textAlign: "center"}}>Welcome to Peak Conditions,
                            the definitive weather forecasting tool designed
                            with the unique needs of mountaineers, hikers, hunters, and nature scientists in mind. Our
                            application is specifically tailored to those who brave the challenging and often
                            unpredictable conditions of mountainous terrains. Stay safer in the back country by using
                            the Peak Conditions weather app when planning your next adventure.
                        </Text>
                    </Center>
                    <Center>
                        <Stack>
                            <Center>
                                <Title>Our Mission</Title>
                            </Center>
                            <Center>
                                <Text style={{width: "80vw", margin: "auto", textAlign: "center"}}>
                                    At Peak Conditions, we understand the critical importance of accurate and
                                    location-specific weather information for mountain enthusiasts and professionals.
                                    Traditional weather apps and forecasts often fall short when it comes to mountains.
                                    They typically provide data for nearby towns, which can be misleading due to the
                                    significant differences in weather caused by changes in elevation, topography, and
                                    the distinct microclimates of mountainous regions. Our mission is to bridge this gap
                                    by offering precise weather forecasts that cater to the exact
                                    location you plan to explore.
                                </Text>
                            </Center>
                        </Stack>
                    </Center>
                    <Center>

                    </Center>
                </AppShell.Main>
            </AppShell>
        </>
    );
}