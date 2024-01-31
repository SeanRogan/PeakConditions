package com.capstone.D424.service;

import org.jsoup.nodes.Document;

public interface WebScraperService {
    Document scrapeDocument(String query);
}
