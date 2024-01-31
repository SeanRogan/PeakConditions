import {useEffect} from "react";
import {wait} from "next/dist/lib/wait";
import {useAuthContext} from "@/app/context/AuthContext";
import {Text} from '@mantine/core';
import {NavigationProps} from "@/app/util/types";

export default function SearchFailure({onNavigate}: NavigationProps) {
    const auth = useAuthContext();
    useEffect(() => {
        //wait 2..3 sec then redirect back
        wait(2500).then(r => auth.user ? onNavigate('AuthenticatedHome') : onNavigate('home'))
    })
    return (
        <>
            <Text>Im sorry but the search failed please try again!</Text>
        </>
    );
}