package com.capstone.D424.service.impl;

import com.capstone.D424.dto.MountainPeakChangeRequest;
import com.capstone.D424.dto.ProfileChangeRequest;
import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.UserProfile;
import com.capstone.D424.repository.MountainPeakRepository;
import com.capstone.D424.repository.UserProfileRepository;
import com.capstone.D424.service.ProfileService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
@Slf4j
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final UserProfileRepository profileRepository;
    private final MountainPeakRepository peakRepository;
    @Override
    public ResponseEntity<UserProfile> saveProfile(ProfileChangeRequest profileChanges) {
        UserProfile profile = null;
        Optional<UserProfile> optionalUserProfile = profileRepository.getUserProfileByProfileId(profileChanges.getProfileId());
        if(optionalUserProfile.isPresent()) {
            profile = optionalUserProfile.get();
            profile.setMinTemp(profileChanges.getMinTemp());
            profile.setMaxTemp(profileChanges.getMaxTemp());
            profile.setMaxWind(profileChanges.getMaxWind());
            profile.setPreferClear(profileChanges.isPreferClear());
            profile.setPreferSomeClouds(profileChanges.isPreferSomeClouds());
            profile.setPreferCloudy(profileChanges.isPreferCloudy());
            profile.setPreferRainShowers(profileChanges.isPreferRainShowers());
            profile.setPreferLightRain(profileChanges.isPreferLightRain());
            profile.setPreferModRain(profileChanges.isPreferModRain());
            profile.setPreferRiskTstorm(profileChanges.isPreferRiskTstorm());
            profile.setPreferSnowShowers(profileChanges.isPreferSnowShowers());
            profile.setPreferLightSnow(profileChanges.isPreferLightSnow());
            profile.setPreferHeavySnow(profileChanges.isPreferHeavySnow());
        }
        if(profile != null) return new ResponseEntity<>(profileRepository.save(profile), HttpStatus.OK);
        else {
            log.warn("the profile was null in the saveProfile method of the profileService, ensure there is a valid profile ID being passed in the request.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @Override
    public ResponseEntity<UserProfile> getProfile(Long profileId) {
        Optional<UserProfile> profile = profileRepository.getUserProfileByProfileId(profileId);
        return profile.map(userProfile -> new ResponseEntity<>(userProfile, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @Override
    public ResponseEntity<UserProfile> deletePeakFromFavs(MountainPeakChangeRequest deleteRequest) {
        Optional<UserProfile> profile = profileRepository.getUserProfileByProfileId(deleteRequest.getProfileId());
        if(profile.isPresent()) {
            UserProfile userProfile = profile.get();
            Set<MountainPeak> favPeaks = userProfile.getFavoritePeaks();
            favPeaks.removeIf(peak -> peak.getPeakId().equals(deleteRequest.getPeakId()));
            userProfile.setFavoritePeaks(favPeaks);
            return new ResponseEntity<>(profileRepository.save(userProfile), HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @Override
    public ResponseEntity<UserProfile> savePeakToFavs(MountainPeakChangeRequest saveRequest) {
        Optional<MountainPeak> peak = peakRepository.getPeakByPeakId(saveRequest.getPeakId());
        Optional<UserProfile> profile = profileRepository.getUserProfileByProfileId(saveRequest.getProfileId());
        if(profile.isPresent() && peak.isPresent()) {
            UserProfile userProfile = profile.get();
            MountainPeak mountainPeak = peak.get();
            Set<MountainPeak> favPeaks = userProfile.getFavoritePeaks();
            favPeaks.add(mountainPeak);
            userProfile.setFavoritePeaks(favPeaks);
            return new ResponseEntity<>(profileRepository.save(userProfile), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

