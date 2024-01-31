package com.capstone.D424.repository;

import com.capstone.D424.entities.MountainSubRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MountainSubRangeRepository extends JpaRepository<MountainSubRange, Long> {
    @Query("SELECT sr FROM MountainSubRange sr WHERE sr.homeRange.rangeId = ?1")
    Optional<List<MountainSubRange>> getMountainSubRangesByHomeRangeId(Long rangeId);

}
