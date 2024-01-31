'use client';
import React, {createContext, useContext, useState} from 'react';
import {AuthContextType, AuthProviderProps, User, UserProfile} from "@/app/util/types";
import {axiosInstance} from '@/app/util/axiosInstance';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuthContext = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuthContext must be used within an AuthProvider');
    }
    return context;
}

export const AuthProvider = ({children}: AuthProviderProps) => {
    const [user, setUser] = useState<User | null>(null);
    const [profile, setProfile] = useState<UserProfile | undefined>(user?.profile);
    const loginUser = async (loginCredentials: { email: string; password: string }) => {
        try {
            const res = await axiosInstance.post('/auth/login', loginCredentials);
            const user = res.data.user;
            if (user) {
                setUser(user);
                setProfile(user.profile);
                console.log("post successful, response: " + JSON.stringify(user));
                return true;
            } else return false;
        } catch (err) {
            console.error("there was an error during the axios request");
            console.log(err);
            return false;
        }
    };
    const registerUser = async (registrationCredentials: { username: string; email: string; password: string; role: string; }) => {

        try {
            const res = await axiosInstance.post('/auth/register', registrationCredentials);

            if (res.data && res.data.authenticationResponse && res.data.success) {
                const user = res.data.authenticationResponse.user;
                console.log("Setting user as:", user);
                setUser(user);
                setProfile(user.profile);
                return res.data; // Return the entire response data
            } else {
                console.log("Registration response without success:", res.data);
                return {success: false}; // Return an object indicating failure
            }
        } catch (err) {
            console.error("There was an error during the axios registration request", err);
            return {success: false}; // Return an object indicating failure
        }

    };

    const logoutUser = () => {
        console.log("user object is currently: " + user);
        console.log("setting user to null");
        setUser(null);
        console.log("user is now: " + user);
    };
    const updateProfile = async () => {
        await axiosInstance.get(`/profile/${user?.profile.profileId}`)
            .then(res => {
                console.log('new profile :' + JSON.stringify(res.data));
                setProfile(res.data);
            })
            .catch(err => console.log(err));
    }
    return (
        <AuthContext.Provider value={{loginUser, logoutUser, registerUser, user, profile, setProfile, updateProfile}}>
            {children}</AuthContext.Provider>);


}
