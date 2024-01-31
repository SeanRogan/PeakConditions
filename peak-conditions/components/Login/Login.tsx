'use client';
import React, {useState} from 'react';
import {Alert, AppShell, Box, Button, Group, PasswordInput, Text, TextInput} from '@mantine/core';
import {useForm} from '@mantine/form';
import {useAuthContext} from "@/app/context/AuthContext";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";
import AuthenticatedNavBar from "@/components/DynamicNavbar/AuthenticatedNavBar";
import {NavigationProps} from "@/app/util/types";
import {useFavoritesContext} from "@/app/context/FavoritesContext";

export default function Login({onNavigate}: NavigationProps) {

    const form = useForm({
        name: 'login-form',
        initialValues: {

            email: '',
            password: '',
        },

        validate: {
            email: (value) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
            password: (value) => (value.length >= 8 ? null : 'Password must be at least 8 characters'),
        },
    });
    const [errorShown, setErrorShown] = useState(false);
    const auth = useAuthContext();
    const fav = useFavoritesContext();

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {

        event.preventDefault();
        const errors = form.validate();
        if (errors.hasErrors) {
            console.log("bad login credentials, form rejected")
            return;
        }
        console.log(form.values);
        const ok = await auth.loginUser(form.values);
        console.log("login ok? : " + ok);
        if (ok) {
            // redirect
            onNavigate('authenticatedHome')
        } else {
            setErrorShown(true);
        }
    };

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
                    <Box maw={340} mx="auto">
                        <Text size="xl">Log in to your account</Text>
                        <form onSubmit={handleSubmit}>
                            <TextInput
                                withAsterisk
                                label="Email"
                                placeholder="your@email.com"
                                {...form.getInputProps('email')}
                            />
                            <PasswordInput
                                withAsterisk
                                label="Password"
                                placeholder="******************"
                                {...form.getInputProps('password')}
                            />
                            <Group justify="center" mt="md">
                                <Button type="submit">Submit</Button>
                                <Button type='button' onClick={() => {
                                    onNavigate('register')
                                }}>Dont have an account? Sign up here</Button>
                            </Group>
                        </form>
                        {
                            errorShown && (
                                <Alert
                                    withCloseButton
                                    closeButtonLabel="Dismiss"
                                    onClose={() => setErrorShown(false)}
                                    title="Login failed."

                                >
                                    The email address and/or password was incorrect. please try again.
                                </Alert>
                            )
                        }
                    </Box>
                </AppShell.Main>
            </AppShell>
        </>
    );
}