'use client';
import React, {createContext, useContext, useState} from 'react';
import {ExtendedForecastResponse, UserReportContextType, UserReportProviderProps} from "@/app/util/types";

const UserReportContext = createContext<UserReportContextType | undefined>(undefined);

export const useUserReportContext = () => {
    const context = useContext(UserReportContext);
    if (context === undefined) {
        throw new Error('useForecastContext must be used within an Provider');
    }
    return context;
}

export const UserReportProvider = ({children}: UserReportProviderProps) => {
    const [peakIds, setPeakIds] = useState<number[]>([]);
    const [reportData, setReportData] = useState<ExtendedForecastResponse[] | ExtendedForecastResponse | undefined>(undefined);

    return (
        <UserReportContext.Provider
            value={{peakIds, setPeakIds, reportData, setReportData}}>{children}</UserReportContext.Provider>);


}
