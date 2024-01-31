'use client';
import {
    AppShell,
    Button,
    Group,
    Image,
    NumberInput,
    rem,
    Switch,
    Text,
    ThemeIcon,
    Title,
    useMantineTheme
} from '@mantine/core';
import {useAuthContext} from "@/app/context/AuthContext";
import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import {IconCheck, IconTemperatureMinus, IconTemperaturePlus, IconWind, IconX,} from "@tabler/icons-react";
import React, {useEffect, useState} from "react";
import {axiosInstance} from '@/app/util/axiosInstance';
import {NavigationProps, User, UserProfile} from "@/app/util/types";
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function WeatherPreferencesPage({onNavigate}: NavigationProps) {

    const theme = useMantineTheme();
    const [maxTemp, setMaxTemp] = useState<number | undefined>(80);
    const [minTemp, setMinTemp] = useState<number | undefined>(30);
    const [maxWind, setMaxWind] = useState<number | undefined>(20);
    const [preferLightRain, setPrefersLightRain] = useState<boolean | undefined>(false);
    const [preferRainShowers, setPrefersRainShowers] = useState<boolean | undefined>(false);
    const [preferModRain, setPrefersModRain] = useState<boolean | undefined>(false);
    const [preferRiskTstorm, setPrefersRiskTstorm] = useState<boolean | undefined>(false);
    const [preferLightSnow, setPrefersLightSnow] = useState<boolean | undefined>(false);
    const [preferSnowShowers, setPrefersSnowShowers] = useState<boolean | undefined>(false);
    const [preferHeavySnow, setPrefersHeavySnow] = useState<boolean | undefined>(false);
    const [preferClear, setPrefersClear] = useState<boolean | undefined>(true);
    const [preferSomeClouds, setPrefersSomeClouds] = useState<boolean | undefined>(true);
    const [preferCloudy, setPrefersCloudy] = useState<boolean | undefined>(false);
    const auth = useAuthContext();
    const fav = useFavoritesContext();

    useEffect(() => {
        // Function to fetch profile data
        const fetchProfile = async (profileId) => {
            try {
                const response = await axiosInstance.get('/profile/' + profileId).then((res) => {
                    auth.setProfile(res.data);
                    console.log('profile retrieved: ' + JSON.stringify(res.data));
                });
                console.log("profile request was successful: " + JSON.stringify(response));
            } catch (err) {
                console.log(err);
            }
        };
        const user: User | null = auth.user;
        // Only fetch profile if it's not already in the context
        if (user && (!auth.profile || auth.profile.last_update !== user.profile.last_update)) {
            fetchProfile(user.profile.profileId).then(r => console.log(JSON.stringify(r)));
        }
    }, [auth.user, auth.setProfile]); // Dependencies: user and setProfile function

    useEffect(() => {
        const profile: UserProfile | undefined = auth.profile;
        if (profile !== undefined) {
            setMaxTemp(profile.maxTemp);
            setMinTemp(profile.minTemp);
            setMaxWind(profile.maxWind);
            setPrefersHeavySnow(profile.preferHeavySnow);
            setPrefersLightSnow(profile.preferLightSnow);
            setPrefersSnowShowers(profile.preferSnowShowers);
            setPrefersClear(profile.preferClear);
            setPrefersSomeClouds(profile.preferSomeClouds);
            setPrefersCloudy(profile.preferCloudy);
            setPrefersRainShowers(profile.preferRainShowers);
            setPrefersLightRain(profile.preferModRain);
            setPrefersModRain(profile.preferModRain);
            setPrefersRiskTstorm(profile.preferRiskTstorm);
        }
    }, [auth.profile])

    const saveProfile = async () => {

        const profileDTO: {
            minTemp: number | undefined;
            maxTemp: number | undefined;
            maxWind: number | undefined;
            profileId: number | undefined;
            preferClear: any;
            preferLightRain: any;
            preferSnowShowers: any;
            preferModRain: any;
            preferCloudy: any;
            preferRiskTstorm: any;
            preferLightSnow: any;
            preferHeavySnow: any;
            preferRainShowers: any;
            preferSomeClouds: any
        } = {
            'profileId': auth.user?.profile.profileId,
            'maxTemp': maxTemp,
            'minTemp': minTemp,
            'maxWind': maxWind,
            'preferClear': preferClear,
            'preferSomeClouds': preferSomeClouds,
            'preferCloudy': preferCloudy,
            'preferRainShowers': preferRainShowers,
            'preferLightRain': preferLightRain,
            'preferModRain': preferModRain,
            'preferRiskTstorm': preferRiskTstorm,
            'preferSnowShowers': preferSnowShowers,
            'preferLightSnow': preferLightSnow,
            'preferHeavySnow': preferHeavySnow
        }
        await axiosInstance.put('/profile', profileDTO).then((res) => {
            console.log('put request was successful, response: ' + JSON.stringify(res.data));
            auth.setProfile(res.data as UserProfile);
            //auth.updateProfile();
            onNavigate('authenticatedHome');
        }).catch((err) => {
            console.log(err)
        });
    }
    return (
        <>
            <AppShell
                header={{height: 70}}
                padding="md">
                <AppShell.Header>
                    <AuthenticatedNavBar onNavigate={onNavigate} onHomeClick={fav.fetchFavorites}/>
                </AppShell.Header>
                <AppShell.Main>
                    <Image radius="md"
                           src={"/mountainCloudy.jpg"}
                           h={200}
                           fallbackSrc="https://placehold.co/600x400?text=Placeholder"/>
                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <Title>
                            Weather Preferences
                        </Title>

                        <Button onClick={saveProfile}>
                            Save Profile
                        </Button>
                    </Group>
                    <Title order={2} style={{paddingRight: '60px', paddingLeft: '60px'}}>select the conditions you wish
                        to encounter during your adventures</Title>
                    <NumberInput label="Minimum Temperature"
                                 leftSection={<IconTemperatureMinus style={{width: rem(20), height: rem(20)}}
                                                                    stroke={1.5}/>}
                                 placeholder="Choose the lowest temperatures you'd like to encounter"
                                 clampBehavior="strict"
                                 min={-35}
                                 max={120}
                                 style={{paddingRight: '60px', paddingLeft: '60px'}}
                                 value={minTemp}
                                 onChange={(value:number) => {
                                     setMinTemp(value)
                                 }}
                    />
                    <NumberInput
                        label="Maximum Temperature"
                        leftSection={<IconTemperaturePlus style={{width: rem(20), height: rem(20)}} stroke={1.5}/>}
                        placeholder="Choose the highest temperatures you'd like to encounter"
                        clampBehavior="strict"
                        min={-35}
                        max={120}
                        style={{paddingRight: '60px', paddingLeft: '60px'}}
                        value={maxTemp}
                        onChange={(value:number) => {
                            setMaxTemp(value)
                        }}/>
                    <NumberInput
                        label="Maximum Wind Speed"
                        leftSection={<IconWind style={{width: rem(20), height: rem(20)}} stroke={1.5}/>}
                        placeholder="Choose the highest winds you'd like to encounter"
                        clampBehavior="strict"
                        min={0}
                        max={80}
                        style={{paddingRight: '60px', paddingLeft: '60px'}}
                        value={maxWind}
                        onChange={(value:number) => {
                            setMaxWind(value)
                        }}/>
                    <Title order={2} style={{paddingRight: '60px', paddingLeft: '60px'}}>select your preferred weather
                        conditions</Title>
                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">

                            <Image
                                src="https://img.icons8.com/ios/50/sun--v1.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>
                        <Text>Sunny and Clear</Text>
                        <Switch
                            checked={preferClear}
                            onChange={(event) => setPrefersClear(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferClear ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>
                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">
                            <Image
                                src="https://img.icons8.com/ios/50/partly-cloudy-day--v1.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>
                        <Text>Partly Cloudy</Text>
                        <Switch
                            checked={preferSomeClouds}
                            onChange={(event) => setPrefersSomeClouds(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferSomeClouds ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>

                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">
                            <Image
                                src="https://img.icons8.com/ios/50/clouds.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>
                        <Text>Cloudy</Text>
                        <Switch
                            checked={preferCloudy}
                            onChange={(event) => setPrefersCloudy(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferCloudy ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>

                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">

                            <Image
                                src="https://img.icons8.com/ios/50/light-rain--v1.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>

                        <Text>Rain Showers</Text>
                        <Switch
                            checked={preferRainShowers}
                            onChange={(event) => setPrefersRainShowers(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferRainShowers ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>
                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">

                            <Image
                                src="https://img.icons8.com/ios/50/rain--v1.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>

                        <Text>Light Rain</Text>
                        <Switch
                            checked={preferLightRain}
                            onChange={(event) => setPrefersLightRain(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferLightRain ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>

                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">
                            <Image
                                src="https://img.icons8.com/ios/50/intense-rain.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>

                        <Text>Moderate Rain</Text>
                        <Switch
                            checked={preferModRain}
                            onChange={(event) => setPrefersModRain(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferModRain ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>

                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">
                            <Image
                                src="https://img.icons8.com/ios/50/storm--v1.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>
                        <Text>Thunder Storms</Text>
                        <Switch
                            checked={preferRiskTstorm}
                            onChange={(event) => setPrefersRiskTstorm(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferRiskTstorm ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>
                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">
                            <Image
                                src="https://img.icons8.com/ios/50/sleet.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>
                        <Text>Snow Showers</Text>
                        <Switch
                            checked={preferSnowShowers}
                            onChange={(event) => setPrefersSnowShowers(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferSnowShowers ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>

                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">
                            <Image
                                src="https://img.icons8.com/ios/50/light-snow--v1.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>
                        <Text>Light Snow</Text>
                        <Switch
                            checked={preferLightSnow}
                            onChange={(event) => setPrefersLightSnow(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferLightSnow ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>

                    <Group style={{
                        height: '100%',
                        width: '100%',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        paddingTop: '16px',
                        paddingRight: '60px',
                        paddingLeft: '60px'
                    }}>
                        <ThemeIcon size="xl">
                            <Image
                                src="https://img.icons8.com/ios/50/snow.png"
                                alt="Weather Icon"
                                width={32}
                                height={32}
                                style={{marginRight: '0px'}} // Adjust spacing as needed
                            />
                        </ThemeIcon>

                        <Text>Heavy Snow</Text>
                        <Switch
                            checked={preferHeavySnow}
                            onChange={(event) => setPrefersHeavySnow(event.currentTarget.checked)}
                            color="teal"
                            size="md"
                            thumbIcon={
                                preferHeavySnow ? (
                                    <IconCheck
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.green[6]}
                                        stroke={3}
                                    />
                                ) : (
                                    <IconX
                                        style={{width: rem(12), height: rem(12)}}
                                        color={theme.colors.red[6]}
                                        stroke={3}
                                    />
                                )
                            }
                        />
                    </Group>

                </AppShell.Main>
            </AppShell></>);
}