'use client';
import React, {createContext, useContext, useState} from 'react';
import {MountainPeak, MountainRange, MountainSubRange, SearchContextType, SearchProviderProps} from "@/app/util/types";

const SearchContext = createContext<SearchContextType | undefined>(undefined);

export const useSearchContext = () => {
    const context = useContext(SearchContext);
    if (context === undefined) {
        throw new Error('useSearchContext must be used within an Provider');
    }
    return context;
}

export const SearchProvider = ({children}: SearchProviderProps) => {
    const [query, setQuery] = useState<string | undefined>("");
    const [searchResultSuccess, setSearchResultSuccess] = useState(false);
    const [peaks, setPeaks] = useState<MountainPeak[] | null>(null);
    const [ranges, setRanges] = useState<MountainRange[] | null>(null);
    const [subRanges, setSubRanges] = useState<MountainSubRange[] | null>(null);
    const [peaksActive, setPeaksActive] = useState(true);
    const [rangesActive, setRangesActive] = useState(true);
    const [subRangesActive, setSubRangesActive] = useState(true);

    return (
        <SearchContext.Provider value={{
            query,
            searchResultSuccess,
            peaks,
            ranges,
            subRanges,
            peaksActive,
            rangesActive,
            subRangesActive,
            setQuery,
            setSearchResultSuccess,
            setPeaks,
            setRanges,
            setSubRanges,
            setPeaksActive,
            setRangesActive,
            setSubRangesActive
        }}>{children}</SearchContext.Provider>);


}
