package com.capstone.D424.repository;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.User;
import com.capstone.D424.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> getUserProfileByProfileOwner(User user);
    Optional<UserProfile> getUserProfileByProfileId(Long profileId);



}
