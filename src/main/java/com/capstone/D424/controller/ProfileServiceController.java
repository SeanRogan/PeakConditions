package com.capstone.D424.controller;

import com.capstone.D424.dto.MountainPeakChangeRequest;
import com.capstone.D424.dto.ProfileChangeRequest;
import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.UserProfile;
import com.capstone.D424.service.ProfileService;
import com.capstone.D424.service.impl.ProfileServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("api/v1/profile")
@Slf4j
public class ProfileServiceController {
    private final ProfileService profileService;
    @Autowired
    ProfileServiceController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable Long id) {
        return profileService.getProfile(id);
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> saveProfile(@RequestBody ProfileChangeRequest profile) {
        return profileService.saveProfile(profile);
    }
    @PutMapping(value = "/peaks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> savePeakToFavs(@RequestBody MountainPeakChangeRequest changeRequest) {
        return profileService.savePeakToFavs(changeRequest);

    }
    @DeleteMapping(value = "/peaks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> deletePeakFromFavs(@RequestBody MountainPeakChangeRequest changeRequest) {
        return profileService.deletePeakFromFavs(changeRequest);
    }
}
