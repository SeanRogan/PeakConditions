'use client';
import React, {createContext, useContext, useState} from 'react';
import {
    DailyForecastResponse,
    ExtendedForecastResponse,
    ForecastContextType,
    ForecastProviderProps
} from "@/app/util/types";

const ForecastContext = createContext<ForecastContextType | undefined>(undefined);

export const useForecastContext = () => {
    const context = useContext(ForecastContext);
    if (context === undefined) {
        throw new Error('useForecastContext must be used within an Provider');
    }
    return context;
}

export const ForecastProvider = ({children}: ForecastProviderProps) => {
    const [peakId, setPeakId] = useState(0);
    const [extended, setExtended] = useState(false);
    const [forecast, setForecast] = useState<ExtendedForecastResponse | DailyForecastResponse | [] >([]);

    return (
        <ForecastContext.Provider value={{
            peakId,
            extended,
            forecast,
            setExtended,
            setForecast,
            setPeakId
        }}>{children}</ForecastContext.Provider>);


}
