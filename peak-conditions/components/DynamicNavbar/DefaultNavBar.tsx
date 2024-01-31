import {ActionIcon, Button, Group, rem, Title} from "@mantine/core";
import {IconHome, IconLogin2} from "@tabler/icons-react";
import {ColorSchemeToggle} from "@/components/ColorSchemeToggle/ColorSchemeToggle";
import React from "react";
import SearchBar from "@/components/Search/SearchBar";
import {NavigationProps} from "@/app/util/types";

export default function DefaultNavBar({onNavigate}: NavigationProps) {
    const loginIcon = <IconLogin2 style={{width: rem(20), height: rem(20)}}/>;

    return (
        <>
            <Group style={{
                height: '100%',
                width: '100%',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '0 16px'
            }}>
                <Group>
                    <ActionIcon onClick={() => {
                        onNavigate('home')
                    }} variant="gradient" size="xl"
                                gradient={{from: "blue", to: "cyan", deg: 75}}><IconHome/></ActionIcon>
                    <Title order={1}>
                        Peak Conditions
                    </Title>
                </Group>
                <Group>
                    <SearchBar onNavigate={onNavigate}/>
                    <Button leftSection={loginIcon} onClick={() => {
                        onNavigate('login')
                    }}>Sign In</Button>
                    <ColorSchemeToggle/>
                </Group>
            </Group>
        </>
    );
}