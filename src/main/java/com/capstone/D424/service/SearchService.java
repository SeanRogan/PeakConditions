package com.capstone.D424.service;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.MountainRange;
import com.capstone.D424.entities.MountainSubRange;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SearchService {

    List<MountainPeak> searchForMountainPeak(String query);

    List<MountainPeak> getAllPeaksInSubRange(Long subRangeId);

    List<MountainSubRange> getAllSubRangesInRange(Long rangeId);

    List<MountainSubRange> searchForMountainSubRange(String query);

    List<MountainRange> searchForMountainRange(String query);
}
