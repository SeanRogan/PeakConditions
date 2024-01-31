import {AppShell, Button, Card, Center, Group, Stack, Title} from "@mantine/core";
import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";
import React, {useEffect, useState} from "react";
import {useAuthContext} from "@/app/context/AuthContext";
import {useSearchContext} from "@/app/context/SearchContext";
import {MountainPeak, NavigationProps} from "@/app/util/types";
import {axiosInstance} from "@/app/util/axiosInstance";
import {useForecastContext} from "@/app/context/ForecastContext";
import {IconArrowLeft} from "@tabler/icons-react";
import {useFavoritesContext} from "@/app/context/FavoritesContext";


export default function SeeAllPeaksResults({onNavigate}: NavigationProps) {
    const auth = useAuthContext();
    const search = useSearchContext();
    const fav = useFavoritesContext();
    const forecast = useForecastContext();
    const [disabledButtons, setDisabledButtons] = useState<{ [key: number]: boolean }>({});
    const [peaks, setPeaks] = useState<MountainPeak[] | undefined>(undefined);

    useEffect(() => {
        setPeaks(search.peaks);
    }, [search.peaks])

    function getForecast(peakId: number) {
        forecast.setForecast([]);
        forecast.setPeakId(peakId);
        let url = auth.user ? `/report/extended/${peakId}` : `/public/report/daily/${peakId}`;
        axiosInstance.get(url).then((res) => {
            forecast.setForecast(res.data);
            if (auth.user) {
                forecast.setExtended(true);
            } else {
                forecast.setExtended(false);
            }
        }).catch((err) => console.log(err));
        onNavigate("forecasts");
    }

    async function addToFavorites(peakId) {
        let dto = {
            "profileId": auth.user?.profile.profileId,
            "peakId": peakId
        }
        await axiosInstance.put("/profile/peaks", dto).then((res) => {
            console.log('server response is:' + JSON.stringify(res.data));
            auth.setProfile(res.data);
        }).catch((err) => console.log(err));
        console.log("auth profile is now :" + JSON.stringify(auth.profile));
        await auth.updateProfile;
        setDisabledButtons(prevState => ({...prevState, [peakId]: true}));
    }

    function handleBackClick() {
        onNavigate("seeAllSubrangesResults");
    }

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
                    <Group justify={"space-between"}>
                        <div style={{width: "10vw"}}>
                            <Button onClick={() => handleBackClick()} leftSection={<IconArrowLeft/>}>Back to
                                Subranges</Button>
                        </div>
                        <div style={{width: "70vw"}}>
                            <Center>
                                <Title>Mountains in Range</Title>
                            </Center>
                        </div>
                        <div style={{width: "10vw"}}></div>
                    </Group>
                    <Stack>
                        {peaks?.map((peak) => (
                            <Card shadow="sm" padding="lg" radius="md" withBorder key={peak.peakId}>
                                <Group justify="space-between">
                                    <Title order={3}>{peak.peakName}</Title>
                                    <Group>
                                        <Button onClick={() => getForecast(peak.peakId)}>Get Forecast</Button>
                                        {auth.user && (<Button onClick={() => {
                                            addToFavorites(peak.peakId)
                                        }} disabled={disabledButtons[peak.peakId]}>Add to
                                            Favorites</Button>)}
                                    </Group>
                                </Group>
                            </Card>
                        ))}
                    </Stack>
                </AppShell.Main>
            </AppShell>
        </>
    );
}