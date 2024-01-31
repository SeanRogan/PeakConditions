package com.capstone.D424.service;

import com.capstone.D424.dto.MountainPeakChangeRequest;
import com.capstone.D424.dto.ProfileChangeRequest;
import com.capstone.D424.entities.UserProfile;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    ResponseEntity<UserProfile> saveProfile(ProfileChangeRequest profileChanges);
    ResponseEntity<UserProfile> getProfile(Long profileId);
    ResponseEntity<UserProfile> deletePeakFromFavs(MountainPeakChangeRequest deleteRequest);
    ResponseEntity<UserProfile> savePeakToFavs(MountainPeakChangeRequest saveRequest);

    }
