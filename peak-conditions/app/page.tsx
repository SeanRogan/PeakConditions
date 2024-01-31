'use client';
import {AuthProvider} from "@/app/context/AuthContext";
import React, {useState} from "react";
import DefaultHomePageContent from "@/components/HomePageContent/DefaultHomePageContent";
import AuthenticatedHomePageContent from "@/components/HomePageContent/AuthenticatedHomePageContent";
import Login from "@/components/Login/Login";
import Registration from "@/components/Registration/Registration";
import FavoritesPage from "@/components/FavoritesPage/FavoritesPage";
import WeatherPreferencesPage from "@/components/PreferencesPages/WeatherPreferencesPage";
import SearchForecastPage from "@/components/Forecast/SearchForecastPage";
import SearchResultPage from "@/components/Search/SearchResultPage";
import {SearchProvider} from "@/app/context/SearchContext";
import {ForecastProvider} from "@/app/context/ForecastContext";
import '@mantine/carousel/styles.css';
import {UserReportProvider} from "@/app/context/UserReportContext";
import UserForecastPage from "@/components/Forecast/UserForecastPage";
import AboutPage from "@/components/AboutPage/AboutPage";
import SeeAllPeaksResults from "@/components/Search/SeeAllPeaksResults";
import SeeAllSubrangesResults from "@/components/Search/SeeAllSubrangesResults";
import {FavoritesProvider} from "@/app/context/FavoritesContext";

export default function LandingPage() {
    const [currentPage, setCurrentPage] = useState('home');
    const navigate = (page: string) => {
        setCurrentPage(page);
    }

    return (<>
        <AuthProvider>
            <SearchProvider>
                <ForecastProvider>
                    <UserReportProvider>
                        <FavoritesProvider>
                        {currentPage === 'about' && (
                            <AboutPage
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'home' && (
                            <DefaultHomePageContent
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'login' && (
                            <Login
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'authenticatedHome' && (
                            <AuthenticatedHomePageContent
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'register' && (
                            <Registration
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'favorites' && (
                            <FavoritesPage
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'profile' && (
                            <WeatherPreferencesPage
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'forecasts' && (
                            <SearchForecastPage
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'searchResults' && (
                            <SearchResultPage
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'selectedForecastResults' && (
                            <UserForecastPage
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'seeAllPeaksResults' && (
                            <SeeAllPeaksResults
                                onNavigate={navigate}
                            />
                        )}
                        {currentPage === 'seeAllSubrangesResults' && (
                            <SeeAllSubrangesResults
                                onNavigate={navigate}
                            />
                        )}
                        </FavoritesProvider>
                    </UserReportProvider>
                </ForecastProvider>
            </SearchProvider>
        </AuthProvider>
    </>);
}
