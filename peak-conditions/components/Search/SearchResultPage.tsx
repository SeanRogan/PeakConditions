'use client';
import React, {useEffect, useState} from "react";
import {AppShell, Button, Card, Center, Group, Stack, Switch, Text, Title} from "@mantine/core";
import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import {useAuthContext} from "@/app/context/AuthContext";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";
import {useSearchContext} from "@/app/context/SearchContext";
import {axiosInstance} from "@/app/util/axiosInstance";
import {useForecastContext} from "@/app/context/ForecastContext";
import {NavigationProps} from "@/app/util/types";
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function SearchResultPage({onNavigate}: NavigationProps) {
    const auth = useAuthContext();
    const search = useSearchContext();
    const fav = useFavoritesContext();
    const forecast = useForecastContext();
    const [disabledButtons, setDisabledButtons] = useState<{ [key: number]: boolean }>({});
    const filteredPeaks = search.peaks?.filter((peak) => peak.peakName);
    let peaks = null;
    let ranges = null;
    let subRanges = null;
    useEffect(() => {
        if (search.searchResultSuccess) {
            peaks = search.peaks;
            ranges = search.ranges;
            subRanges = search.subRanges;
        }
    }, [search]);


    async function addToFavorites(peakId: number) {
        let dto = {
            "profileId": auth.user?.profile.profileId,
            "peakId": peakId
        }
        await axiosInstance.put("/profile/peaks", dto).then((res) => {
            console.log('server response is:' + JSON.stringify(res.data));
            auth.setProfile(res.data);
            setDisabledButtons(prevState => ({...prevState, [peakId]: true}));
        }).catch((err) => console.log(err));
        console.log("auth profile is now :" + JSON.stringify(auth.profile));
        await auth.updateProfile;
    }

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
        onNavigate('forecasts');
    }

    function seeAllSubranges(rangeId: number) {
        search.setSubRanges([]);
        let url = `/public/search/subranges/${rangeId}`;
        axiosInstance.get(url).then((res) => {
                search.setSubRanges(res.data);
                onNavigate('seeAllSubrangesResults');
            }
        ).catch((err) => {
            console.error(err)
        });
    }

    function seeAllPeaks(subrangeId: number) {
        search.setPeaks([]);
        let url = `/public/search/peaks/${subrangeId}`;
        axiosInstance.get(url).then((res) => {
                search.setPeaks(res.data);
                onNavigate('seeAllPeaksResults')
            }
        ).catch((err) => {
            console.error(err)
        });
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
                    {/* Filter Group */}
                    <Group justify="space-between">
                        <Text>Filters</Text>
                        <Group justify="flex-end">
                            <Switch
                                checked={search.peaksActive}
                                onChange={(event) => search.setPeaksActive(event.currentTarget.checked)}
                                label="Peaks"
                            />
                            <Switch
                                checked={search.rangesActive}
                                onChange={(event) => search.setRangesActive(event.currentTarget.checked)}
                                label="Ranges"
                            />
                            <Switch
                                checked={search.subRangesActive}
                                onChange={(event) => search.setSubRangesActive(event.currentTarget.checked)}
                                label="Sub-ranges"
                            />
                        </Group>
                    </Group>
                    {/*No results message*/}
                    {search.peaks?.length < 1 && search.subRanges?.length < 1 && search.ranges?.length < 1 && (
                        <Stack>
                            <Center>
                                <Title>
                                Sorry! There were no results.
                                </Title>
                            </Center>
                        </Stack>
                    )}

                    {/* Peaks Display */}
                    {search.peaksActive && search.peaks && search.peaks.length > 0 && (
                        <Stack>
                            <Title>Peaks</Title>
                            {filteredPeaks.map((peak) => (
                                <Card shadow="sm" padding="lg" radius="md" withBorder key={peak.peakId}>
                                    <Group justify="space-between">
                                        <Stack>
                                            <Title order={3}>{peak.peakName}</Title>
                                        </Stack>
                                        <Group>
                                            <Button onClick={() => getForecast(peak.peakId)}>Get Forecast</Button>
                                            {auth.user && (<Button onClick={() => {
                                                addToFavorites(peak.peakId);
                                            }} disabled={disabledButtons[peak.peakId]}>Add to
                                                Favorites</Button>)}
                                        </Group>
                                    </Group>
                                </Card>
                            ))}
                        </Stack>
                    )}

                    {/* Range Display */}

                    {search.rangesActive && search.ranges && search.ranges.length > 0 && (
                        <Stack>
                            <Title>Ranges:</Title>
                            {search.ranges.map((range) => (
                                <Card shadow="sm" padding="lg" radius="md" withBorder key={range.rangeId}>
                                    <Group justify="space-between">

                                        <Title order={3}>{range.rangeName}</Title>
                                        <Button onClick={() => seeAllSubranges(range.rangeId)}>See all
                                            sub-ranges</Button>
                                    </Group>
                                </Card>
                            ))}
                        </Stack>
                    )}
                    {/* SubRange Display */}
                    {search.subRangesActive && search.subRanges && search.subRanges.length > 0 && (
                        <Stack>
                            <Title>SubRanges: </Title>
                            {search.subRanges.map((subRange) => (
                                <Card shadow="sm" padding="lg" radius="md" withBorder key={subRange.subrangeId}>
                                    <Group justify="space-between">
                                        <Title order={3}>{subRange.rangeName}</Title>
                                        <Button onClick={() => seeAllPeaks(subRange.subrangeId)}>See all peaks in
                                            sub-range</Button>
                                    </Group>
                                </Card>
                            ))}
                        </Stack>
                    )}
                </AppShell.Main>
            </AppShell>
        </>


    );
}