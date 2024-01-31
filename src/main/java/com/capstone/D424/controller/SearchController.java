package com.capstone.D424.controller;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.MountainRange;
import com.capstone.D424.entities.MountainSubRange;
import com.capstone.D424.service.SearchService;
import com.capstone.D424.service.impl.SearchServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Search Controller is a Controller class.
 * The endpoints take a String argument and
 * queries the database for objects with
 * the same name or containing
 * the query pattern within the name.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/public")
@Slf4j
public class SearchController {

    private final SearchService searchService;

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
        if(!searchResults.isEmpty()) {
            return new ResponseEntity<>(searchResults, HttpStatus.OK);
        } else return new ResponseEntity<>(searchResults, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search/subranges/{rangeId}")
    public ResponseEntity<List<MountainSubRange>> getSubRangesInRange(@PathVariable Long rangeId) {
        List<MountainSubRange> ranges = searchService.getAllSubRangesInRange(rangeId);
        if(!ranges.isEmpty()) {
            return new ResponseEntity<>(ranges, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search/peaks/{subRangeId}")
    public ResponseEntity<List<MountainPeak>> getPeaksInSubRange(@PathVariable Long subRangeId) {
        List<MountainPeak> peaks = searchService.getAllPeaksInSubRange(subRangeId);
        if(!peaks.isEmpty()) {
            return new ResponseEntity<>(peaks, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
