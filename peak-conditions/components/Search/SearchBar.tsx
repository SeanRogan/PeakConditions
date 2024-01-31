"use client"
import {useState} from "react";
import {ActionIcon, Group, TextInput} from "@mantine/core";
import {IconSearch} from "@tabler/icons-react";
import {useSearchContext} from "@/app/context/SearchContext";
import {axiosInstance} from "@/app/util/axiosInstance";
import {NavigationProps} from "@/app/util/types";

export default function SearchBar({onNavigate}: NavigationProps) {
    const searchContext = useSearchContext();
    const [query, setQuery] = useState("");
    const axi = axiosInstance;
    const handleSearch = async () => {
        //search logic
        const searchQuery = `/public/search?query=${query}`;
        axi.get(searchQuery).then((res) => {
            searchContext.setQuery(query);
            console.log("search was successful, response: " + JSON.stringify(res.data));
            searchContext.setPeaks(res.data.peaks);
            searchContext.setRanges(res.data.ranges);
            searchContext.setSubRanges(res.data.subranges);
            searchContext.setSearchResultSuccess(true);
            onNavigate('searchResults');
        }).catch((err) => {
            console.log(err)
        });
    };


    return (
        <Group style={{margin: '20px 0', width: '280px'}}>
            <TextInput
                radius="lg"
                value={query}
                onChange={(event) => setQuery(event.currentTarget.value)}
                placeholder="Search for your favorite peaks!"
                style={{flexGrow: 1}}
            />
            <ActionIcon
                variant="gradient"
                size="lg"
                aria-label="search button"
                gradient={{from: 'blue', to: 'cyan', deg: 90}}
                onClick={() => handleSearch()}>
                <IconSearch/>
            </ActionIcon>
        </Group>
    );
}