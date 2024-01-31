'use client';

import {ActionIcon, Tooltip, useMantineColorScheme} from '@mantine/core';
import {IconMoon, IconSun} from '@tabler/icons-react';

export function ColorSchemeToggle() {
    const {colorScheme, toggleColorScheme} = useMantineColorScheme();

    return (
        <Tooltip label="Toggle dark-mode.">
            <ActionIcon
                variant="default"
                onClick={() => toggleColorScheme()}>
                {colorScheme === "dark" ? <IconMoon/> : <IconSun/>}
            </ActionIcon>
        </Tooltip>
    );
}
