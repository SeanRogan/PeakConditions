import {AppShell, Button, Card, Center, Group, Stack, Title} from "@mantine/core";
import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";
import React, {useEffect, useState} from "react";
import {useAuthContext} from "@/app/context/AuthContext";
import {useSearchContext} from "@/app/context/SearchContext";
import {MountainSubRange, NavigationProps} from "@/app/util/types";
import {axiosInstance} from "@/app/util/axiosInstance";
import {IconArrowLeft} from '@tabler/icons-react';
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function SeeAllSubrangesResults({onNavigate}: NavigationProps) {
    const auth = useAuthContext();
    const search = useSearchContext();
    const fav = useFavoritesContext();
    const [subRanges, setSubRanges] = useState<MountainSubRange[] | undefined>(undefined);

    useEffect(() => {
        setSubRanges(search.subRanges);
    }, [search.subRanges])

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

    function handleBackClick() {
        onNavigate('searchResults')
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
                    <Stack>
                        <Group justify={'space-between'}>
                            <div style={{width: '30vw'}}>
                                <Button onClick={() => handleBackClick()} leftSection={<IconArrowLeft/>}>search
                                    results</Button>
                            </div>
                            <div style={{width: '30vw'}}>
                                <Center>
                                    <Title>SubRanges</Title>
                                </Center>
                            </div>
                            <div style={{width: '30vw'}}></div>
                        </Group>
                        {subRanges?.map((subRange) => (
                            <Card shadow="sm" padding="lg" radius="md" withBorder key={subRange.subrangeId}>
                                <Group justify="space-between">
                                    <Title order={3}>{subRange.rangeName}</Title>
                                    <Button onClick={() => seeAllPeaks(subRange.subrangeId)}>See all peaks in
                                        sub-range</Button>
                                </Group>
                            </Card>
                        ))}
                    </Stack>
                </AppShell.Main>
            </AppShell>
        </>
    );
}