import React, {createContext, ReactNode, useContext, useState} from 'react';
import {
    DailyForecastResponse,
    ExtendedForecastResponse,
    FavoritesContextType,
    FavoritesProviderProps
} from "@/app/util/types";
import {axiosInstance} from "@/app/util/axiosInstance";
import {useAuthContext} from "@/app/context/AuthContext";


const FavoritesContext = createContext<FavoritesContextType | undefined>(undefined);

export const useFavoritesContext = () => {
    const context = useContext(FavoritesContext);
    if (context === undefined) {
        throw new Error('useFavoritesContext must be used within a FavoritesProvider');
    }
    return context;
};

export const FavoritesProvider= ({ children }: FavoritesProviderProps) => {
    const auth = useAuthContext();
    const [groupedReports, setGroupedReports] = useState<{ [p: string]: DailyForecastResponse[] }>({});

    const checkWeather = (weather: any, prefs: any) => {
        // Function to check if a weather condition matches user preferences. Takes the userProfile as prefs and the weather report as weather
        const windSpeedMatch = weather.windConditions.match(/\d+/); // Extracts numbers from windConditions
        const windSpeed = windSpeedMatch ? parseInt(windSpeedMatch[0]) : 0; // Converts to number, defaults to 0 if no match
        const highTemp = parseInt(weather.high); // Convert high temp to number
        const lowTemp = parseInt(weather.low); // Convert low temp to number
        return (
            ((prefs.preferClear && weather.weatherConditions === 'clear') ||
                (prefs.preferSomeClouds && weather.weatherConditions === 'some clouds') ||
                (prefs.preferCloudy && weather.weatherConditions === 'cloudy') ||
                (prefs.preferRainShowers && weather.weatherConditions === 'rain shwrs') ||
                (prefs.preferLightRain && weather.weatherConditions === 'light rain') ||
                (prefs.preferModRain && weather.weatherConditions === 'mod rain') ||
                (prefs.preferRiskTstorm && weather.weatherConditions === 'risk Tstorm') ||
                (prefs.preferSnowShowers && weather.weatherConditions === 'snow shwrs') ||
                (prefs.preferLightSnow && weather.weatherConditions === 'light snow') ||
                (prefs.preferHeavySnow && weather.weatherConditions === 'heavy snow')
            ) &&
            (prefs.maxTemp >= highTemp && prefs.minTemp <= lowTemp) &&
            (prefs.maxWind >= windSpeed));
    };

    const fetchFavorites = async () => {
        const favPeakIds = auth.profile?.favoritePeaks.map(peak => peak.peakId) || [];
        if (favPeakIds.length === 0) {
            setGroupedReports({});
            return;
        }
        if (favPeakIds.length > 0) {
            const reports: any = await Promise.all(favPeakIds.map(peakId => {
                const url = `/report/extended/${peakId}`;
                return axiosInstance.get<DailyForecastResponse>(url)
                    .then(res => res.data)
                    .catch(err => console.error(err));
            }));
            const filteredArray: DailyForecastResponse[] = [];
            reports.map(peakReportArray => {
                    peakReportArray.map(peakReport => {
                        const AM = peakReport.forecastData.AM;
                        const PM = peakReport.forecastData.PM;
                        const NT = peakReport.forecastData.Night;

                        const amResult = checkWeather(AM, auth.profile);
                        const pmResult = checkWeather(PM, auth.profile);
                        const ntResult = checkWeather(NT, auth.profile);
                        if (amResult || pmResult || ntResult) {
                            filteredArray.push(peakReport);
                        }
                    });
                }
            );
            const newGroupedReports = {};
            if(filteredArray) {
                filteredArray.forEach(dailyForecast => {
                            console.log("extended forecast =" + JSON.stringify(dailyForecast))
                            const peakName = dailyForecast?.forecastData.AM.peakName;
                            if (!newGroupedReports[peakName]) {
                                newGroupedReports[peakName] = [];
                            }
                            newGroupedReports[peakName].push(dailyForecast);
                        });
                    };

            console.log("grouped reports is =" + JSON.stringify(newGroupedReports));
            setGroupedReports(newGroupedReports);
            return newGroupedReports;
        }
    }
    return (
        <FavoritesContext.Provider value={{ groupedReports, fetchFavorites }}>
            {children}
        </FavoritesContext.Provider>
    );
};
