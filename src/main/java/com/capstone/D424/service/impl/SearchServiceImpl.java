package com.capstone.D424.service.impl;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.MountainRange;
import com.capstone.D424.entities.MountainSubRange;
import com.capstone.D424.repository.MountainPeakRepository;
import com.capstone.D424.repository.MountainRangeRepository;
import com.capstone.D424.repository.MountainSubRangeRepository;
import com.capstone.D424.service.SearchService;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
@Transactional
public class SearchServiceImpl implements SearchService {

    private final MountainRangeRepository mountainRangeRepository;
    private final MountainSubRangeRepository mountainSubRangeRepository;
    private final MountainPeakRepository mountainPeakRepository;
    @Autowired
    public SearchServiceImpl(MountainPeakRepository mountainPeakRepository,
                             MountainRangeRepository mountainRangeRepository,
                             MountainSubRangeRepository mountainSubRangeRepository) {
        this.mountainPeakRepository = mountainPeakRepository;
        this.mountainRangeRepository = mountainRangeRepository;
        this.mountainSubRangeRepository = mountainSubRangeRepository;
    }
    @Override
    public List<MountainPeak> searchForMountainPeak(String query) {
        List<MountainPeak> searchResults;
        query = query.toLowerCase().trim().replace("+"," ");
         searchResults = mountainPeakRepository.findAll();
        if (!searchResults.isEmpty()) {
            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            Iterator<MountainPeak> iterator = searchResults.iterator();
            while(iterator.hasNext()) {
                MountainPeak p = iterator.next();
                Matcher matcher = pattern.matcher(p.getPeakName());
                if(!matcher.find()) {
                    iterator.remove();
                }
            } return searchResults;
        } else return null;
    }
    @Override
    public List<MountainRange> searchForMountainRange(String query) {
        List<MountainRange> ranges;
                query = query.toLowerCase().trim().replace("+"," ");
        //collect all ranges
        ranges = mountainRangeRepository.findAll();
        //if a result was found,
        // iterate through the list and check
        // if the name matches to a regex of the query,
        // if it does not, remove it from the list
        if(!ranges.isEmpty()) {
            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            Iterator<MountainRange> iterator = ranges.iterator();
            while(iterator.hasNext()) {
                MountainRange m = iterator.next();
                Matcher matcher = pattern.matcher(m.getRangeName());
                if (!matcher.find()) {
                    iterator.remove();
                }
            }
            return ranges;
        }//else if no response to the query is found*/
        log.info("No mountain range found for query: " + query);
        return null;

    }
    @Override
    public List<MountainSubRange> searchForMountainSubRange(String query) {
        List<MountainSubRange> results;
        query = query.toLowerCase().trim().replace("+"," ");
        results = mountainSubRangeRepository.findAll();
        //if a result was found,
        // iterate through the list and check
        // if the name matches to a regex of the query,
        // if it does not, remove it from the list
        if(!results.isEmpty()) {
            Iterator<MountainSubRange> iterator = results.iterator();
            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            while(iterator.hasNext()){
                MountainSubRange m = iterator.next();
                Matcher matcher = pattern.matcher(m.getRangeName());
                if(!matcher.find()) {
                    iterator.remove();
                }
            } return results;
        } return results;
    }
    @Override
    public List<MountainPeak> getAllPeaksInSubRange(Long subRangeId) {
        List<MountainPeak> results = new ArrayList<>();
        Optional<List<MountainPeak>> peaks;
        try{
            //collect all peaks
            peaks = mountainPeakRepository.getMountainPeaksBySubRangeId(subRangeId);
            //if peaks exist add to List and return
            peaks.ifPresent(results::addAll);
            log.info("Mountain peaks found");
        } catch (Exception e) {
            log.error(e.toString());
        }
        return results;
    }

    @Override
    public List<MountainSubRange> getAllSubRangesInRange(Long rangeId) {
        List<MountainSubRange> results = new ArrayList<>();
        Optional<List<MountainSubRange>> subRanges;
        try {
            subRanges = mountainSubRangeRepository.getMountainSubRangesByHomeRangeId(rangeId);
            subRanges.ifPresent(results::addAll);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return results;
    }
}
