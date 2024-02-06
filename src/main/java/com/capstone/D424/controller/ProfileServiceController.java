package com.capstone.D424.controller;

import com.capstone.D424.dto.MountainPeakChangeRequest;
import com.capstone.D424.dto.ProfileChangeRequest;
import com.capstone.D424.entities.UserProfile;
import com.capstone.D424.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class handling HTTP requests related to user profiles.
 * Provides endpoints for retrieving and updating user profile and their favorite mountain peaks.
 */
@RestController
@RequestMapping("api/v1/profile")
@Slf4j
public class ProfileServiceController {
    private final ProfileService profileService;

    /**
     * Constructs a ProfileServiceController with the specified ProfileService.
     *
     * @param profileService The profile service used to manage user profiles.
     */
    @Autowired
    ProfileServiceController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Retrieves a user profile by its ID.
     *
     * @param id The ID of the user profile to retrieve.
     * @return ResponseEntity containing the user profile.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable Long id) {
        return profileService.getProfile(id);
    }

    /**
     * Updates or saves the user profile with the provided profile information.
     *
     * @param profile The profile change request containing updated user profile information.
     * @return ResponseEntity containing the updated user profile.
     */
    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> saveProfile(@RequestBody ProfileChangeRequest profile) {
        return profileService.saveProfile(profile);
    }

    /**
     * Adds a mountain peak to the user's list of favorite peaks.
     *
     * @param changeRequest The mountain peak change request containing information about the peak to add.
     * @return ResponseEntity containing the updated user profile with the new favorite peak added.
     */
    @PutMapping(value = "/peaks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> savePeakToFavs(@RequestBody MountainPeakChangeRequest changeRequest) {
        return profileService.savePeakToFavs(changeRequest);

    }

    /**
     * Removes a mountain peak from the user's list of favorite peaks.
     *
     * @param changeRequest The mountain peak change request containing information about the peak to remove.
     * @return ResponseEntity containing the updated user profile with the specified peak removed from favorites.
     */
    @DeleteMapping(value = "/peaks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> deletePeakFromFavs(@RequestBody MountainPeakChangeRequest changeRequest) {
        return profileService.deletePeakFromFavs(changeRequest);
    }
}
