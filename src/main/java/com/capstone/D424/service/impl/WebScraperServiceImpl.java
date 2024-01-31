package com.capstone.D424.service.impl;

import com.capstone.D424.service.WebScraperService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * The DataScraper class handles the web-scraping process by using the Jsoup library
 */
@Component
@Slf4j
public class WebScraperServiceImpl implements WebScraperService {

    WebScraperServiceImpl() {
    }

    /**
     * @param uri - a webpage uri to scrape data from.
     * @return Document - returns an HTML document object, scraped from the uri argument.
     */
    public Document scrapeDocument(String uri) {
        try {
            return Jsoup.connect(uri).get();
        } catch (Exception e) {
            log.error("An error has occured in MountainWeatherScraper.api.webscraper.DataScraper.scrapeDocument() : \n" +
                    e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

}
