package com.capstone.D424.controller;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.MountainRange;
import com.capstone.D424.entities.MountainSubRange;
import com.capstone.D424.service.SearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class handling HTTP requests related to the search function of the web app.
 * Provides endpoints for general searches and endpoints for searching for specific subranges or mountain peaks.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/public")
@Slf4j
public class SearchController {

    private final SearchService searchService;

    /**
     * Handles general search for mountain peaks and ranges requests with the provided query string.
     *
     * @param query the search term entered by the user
     * @return a responseEntity is returned with a status code.
     * The response body contains search results if the search was valid.
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, List>> search(@RequestParam String query) {
        List<MountainPeak> peaks;
        List<MountainRange> ranges;
        List<MountainSubRange> subranges;
        peaks = searchService.searchForMountainPeak(query);
        ranges = searchService.searchForMountainRange(query);
        subranges = searchService.searchForMountainSubRange(query);
        Map<String, List> searchResults = new HashMap<>();
        searchResults.put("peaks", peaks);
        searchResults.put("ranges", ranges);
        searchResults.put("subranges", subranges);
        if (!searchResults.isEmpty()) {
            return new ResponseEntity<>(searchResults, HttpStatus.OK);
        } else return new ResponseEntity<>(searchResults, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles searches for subranges with the subrange id
     *
     * @param rangeId The subrange id field
     * @return a responseEntity is returned with the status code,
     * the response body is the search results if the search was valid.
     */
    @GetMapping("/search/subranges/{rangeId}")
    public ResponseEntity<List<MountainSubRange>> getSubRangesInRange(@PathVariable Long rangeId) {
        List<MountainSubRange> ranges = searchService.getAllSubRangesInRange(rangeId);
        if (!ranges.isEmpty()) {
            return new ResponseEntity<>(ranges, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Handles searches for mountain peaks associated with the given subrange id.
     *
     * @param subRangeId the subrange id of the mountain peak's home range.
     * @return a responseEntity is returned with the status code,
     * the response body is the search results if the search was valid.
     */
    @GetMapping("/search/peaks/{subRangeId}")
    public ResponseEntity<List<MountainPeak>> getPeaksInSubRange(@PathVariable Long subRangeId) {
        List<MountainPeak> peaks = searchService.getAllPeaksInSubRange(subRangeId);
        if (!peaks.isEmpty()) {
            return new ResponseEntity<>(peaks, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
