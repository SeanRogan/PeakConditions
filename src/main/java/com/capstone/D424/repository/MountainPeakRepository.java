package com.capstone.D424.repository;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.MountainSubRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MountainPeakRepository extends JpaRepository<MountainPeak, Long>{
    @Query("SELECT m.uri FROM MountainPeak m WHERE m.peakId = ?1")
    String getPeakUriByPeakId(Long peakId);
    @Query("SELECT m.peakName FROM MountainPeak m WHERE m.peakId = ?1")
    String getPeakNameByPeakId(Long peakId);

    @Query("SELECT m FROM MountainPeak m WHERE m.peakId = ?1")
    Optional<MountainPeak> getPeakByPeakId(Long peakId);
    @Query("SELECT m FROM MountainPeak  m WHERE m.homeSubrange.subrangeId = ?1")
    Optional<List<MountainPeak>> getMountainPeaksBySubRangeId(Long subRangeId);
}
