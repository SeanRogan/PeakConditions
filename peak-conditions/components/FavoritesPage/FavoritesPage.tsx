'use client';
import {AppShell, Button, Center, Checkbox, Group, Image, Space, Text, Title} from '@mantine/core';
import {useAuthContext} from "@/app/context/AuthContext";
import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import React, {useEffect, useState} from "react";
import {MountainPeak, NavigationProps} from "@/app/util/types";
import {axiosInstance} from "@/app/util/axiosInstance";
import {useForecastContext} from "@/app/context/ForecastContext";
import {useUserReportContext} from "@/app/context/UserReportContext";
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function FavoritesPage({onNavigate}: NavigationProps) {
    const forecast = useForecastContext();
    const report = useUserReportContext();
    const auth = useAuthContext();
    const fav = useFavoritesContext();
    const [favorites, setFavorites] = useState<MountainPeak[]>([]);
    const [allSelected, setAllSelected] = useState(false);
    const [selected, setSelected] = useState([]);


    useEffect(() => {
        //check if all values are truthy
        if (selected.every(val => val)) {
            //if yes, set all selected to true
            setAllSelected(true);
        } else {
            //if no set to false.
            setAllSelected(false);
        }
    }, [selected, setAllSelected]);

    useEffect(() => {
        if (auth.profile) {
            setFavorites(auth.profile?.favoritePeaks);
            setSelected(new Array(auth.profile?.favoritePeaks?.length).fill(false));
        }
        console.log('setting favorites in use effect..now:' + JSON.stringify(favorites));
    }, [auth, favorites, setFavorites, setSelected]);

    const handleSelectAll = (event) => {
        const checked = event.target.checked;
        setAllSelected(checked);
        setSelected(selected.map(() => checked));
    }
    const handleSelectPeak = (index) => {
        const selectedBoxes = [...selected];
        selectedBoxes[index] = !selectedBoxes[index];
        setSelected(selectedBoxes);
    }
    const removeFromFavorites = async (mountainId) => {
        let dto = {
            profileId: auth.user?.profile.profileId,
            peakId: mountainId
        };
        try {

            const res = await axiosInstance({
                method: 'delete',
                url: '/profile/peaks',
                data: dto
            });
            auth.setProfile(res.data);
            console.log("profile set: " + JSON.stringify(res.data));
        } catch (err) {
            console.log(err);
        }
    };
    const getForecast = (mountainId) => {
        forecast.setForecast([]);
        const peakId = mountainId;
        let authenticated = !!auth.user;
        forecast.setPeakId(peakId);
        let url = authenticated ? `/report/extended/${peakId}` : `/public/report/daily/${peakId}`;
        axiosInstance.get(url).then((res) => {
            forecast.setForecast(res.data);
            if (authenticated) {
                forecast.setExtended(true);
            } else {
                forecast.setExtended(false);
            }
        }).catch((err) => console.log(err));
        onNavigate('forecasts');
    }
    useEffect(() => {
        console.log(report.peakIds);
    }, [report]);
    const getSingleReport = (peakId) => {
        const peakIds = [];
        peakIds.push(peakId);
        report.setPeakIds(peakIds);
        onNavigate("selectedForecastResults");
    }
    const getSelectedForecasts = () => {
        //create an array of peak Ids from the favorites and selected arrays.
        const selectedPeakIds = favorites
            .filter((_, index) => selected[index])
            .map(peak => peak.peakId);
        // Perform actions with the selectedPeakIds array
        console.log("Selected Peak IDs:", selectedPeakIds);
        // You can add more code here to do something with the selectedPeakIds
        report.setPeakIds(selectedPeakIds);
        onNavigate("selectedForecastResults")
    }
    const renderFavs = (mountain, index) => {
        return (
            <div key={mountain.peakId}>
                <Group
                    style={{
                    height: '100%',
                    width: '100%',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    paddingTop: '16px',
                    paddingRight: '60px',
                    paddingLeft: '60px'
                }}>
                    <Group>
                        <Checkbox
                            checked={selected[index]}
                            onChange={() => handleSelectPeak(index)}
                            label="Select peak"
                        />
                        <Title order={2}>{mountain.peakName}</Title>
                    </Group>
                    <Group>
                        <Button onClick={() => {
                            if (!auth.user) {
                                getForecast(mountain.peakId);
                            } else {
                                getSingleReport(mountain.peakId)
                            }
                        }}>Get Forecast
                        </Button>
                        <Button onClick={() => removeFromFavorites(mountain.peakId)}>
                            Remove
                        </Button>
                    </Group>
                </Group>
            </div>
        )
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
                    <Center>
                        <Title order={2}>My Favorite Peaks</Title>
                    </Center>
                    <Space h="xl"/>
                    <Space h="xl"/>
                    {favorites?.length > 0 ? favorites?.map((mountain, index) => renderFavs(mountain, index)) : (
                        <>
                            <Center>
                                <Title order={4}>No Peaks have been selected.</Title>
                            </Center>
                            <Center>
                                <Text>
                                    Use the search bar to search for mountains and add them to your list of
                                    favorites.
                                </Text>
                            </Center>
                        </>)}
                    <Space h="xl"/>
                    <Space h="xl"/>
                    <Group>
                        <Checkbox
                            checked={allSelected}
                            onChange={(event) => handleSelectAll(event)}
                            label="Select all peaks"
                        />
                        <Button
                            onClick={() => getSelectedForecasts()}>
                            See all selected forecasts
                        </Button>
                    </Group>
                </AppShell.Main>
            </AppShell></>);
}