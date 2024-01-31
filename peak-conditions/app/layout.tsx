import '@mantine/core/styles.css';
import React from 'react';
import {ColorSchemeScript, MantineProvider} from '@mantine/core';
import {theme} from '@/theme';

export const metadata = {
    title: 'Peak Conditions - Mountain Weather App',
    description: 'Weather forecasts for the tops of mountains across the world',
};

export default function RootLayout({children}: { children: any }) {
    return (
        <html lang="en">
        <head>
            <ColorSchemeScript/>
            <link rel="shortcut icon" href="/weathericon.svg"/>
            <meta
                name="viewport"
                content="minimum-scale=1, initial-scale=1, width=device-width, user-scalable=no"
            />

        </head>
        <body>
        <MantineProvider theme={theme}>{children}</MantineProvider>
        </body>
        </html>
    );
}
