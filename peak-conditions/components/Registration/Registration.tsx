'use client';
import React, {useState} from 'react';
import {Alert, AppShell, Box, Button, Group, PasswordInput, Text, TextInput} from '@mantine/core';
import {useForm} from '@mantine/form';
import {useAuthContext} from "@/app/context/AuthContext";
import DefaultNavBar from "@/components/DynamicNavbar/DefaultNavBar";
import {NavigationProps} from "@/app/util/types";

export default function Registration({onNavigate}: NavigationProps) {
    // const [emailState, setEmailState] = useState('');
    // const [passwordFieldState, setPasswordFieldState] = useState('');
    // const [passwordRepeatFieldState, setRepeatFieldState] = useState('');
    const [errorShown, setErrorShown] = useState(false);
    const auth = useAuthContext();
    const form = useForm({
        name: 'registration-form',
        initialValues: {
            username: '',
            email: '',
            password: '',
            passwordRepeat: '',
            role: 'ROLE_USER_PAID'
        },

        validate: {
            email: (value) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
            password: (value) => (value.length >= 8 ? null : 'Please choose a password of 8 characters in length or more.'),
            passwordRepeat: (value, values) => (value === values.password ? null : 'Passswords do not match.'),
        },
    });
    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        // Your form submission logic here
        const errors = form.validate();
        console.log(JSON.stringify(errors))
        if (errors.hasErrors) {
            console.log("invalid registration form rejected." + JSON.stringify(form.values));
            return;
        }
        try {
            // Making API call
            const res = await auth.registerUser(form.values);
            console.log("API response:", JSON.stringify(res));

            // Check the response
            if (res && res.success) {
                form.reset();
                onNavigate('authenticatedHome');
            } else {
                // Handle unsuccessful registration
                setErrorShown(true);
                console.log('Registration was unsuccessful', res);
            }
        } catch (error) {
            // Handle errors during the API call
            setErrorShown(true);
            console.error('Error in registration:', error);
        }
    }

    const alertMessage = "";
    return (

        <>
            <AppShell
                header={{height: 70}}
                padding="md">
                <AppShell.Header>
                    <DefaultNavBar onNavigate={onNavigate}/>
                </AppShell.Header>

                <AppShell.Main>

                    <Box maw={340} mx="auto">
                        <Text size="xl">Sign up for an account</Text>
                        <form onSubmit={handleSubmit}>
                            <TextInput
                                withAsterisk
                                label="Choose a username"
                                placeholder="enter username"
                                {...form.getInputProps('username')}
                            />
                            <TextInput
                                withAsterisk
                                label="Enter your email address"
                                placeholder="enter email address"
                                {...form.getInputProps('email')}
                            />
                            <PasswordInput
                                withAsterisk
                                label="Choose a password"
                                placeholder="enter password"
                                {...form.getInputProps('password')}
                            />
                            <PasswordInput
                                withAsterisk
                                label="Confirm your password"
                                placeholder="confirm password"
                                {...form.getInputProps('passwordRepeat')}
                            />
                            <Group justify="center" mt="md">
                                <Button type="submit">Submit</Button>
                                <Button onClick={() => {
                                    onNavigate("login")
                                }}>Already have an account? Log in here</Button>
                            </Group>
                        </form>
                        {errorShown && (
                            <Alert
                                withCloseButton
                                closeButtonLabel="Dismiss"
                                onClose={() => setErrorShown(false)}
                                title="Registration Failure"
                            >
                                There was a problem with registration. Please try again.
                            </Alert>
                        )}
                    </Box>
                </AppShell.Main>
            </AppShell>

        </>

    );
}