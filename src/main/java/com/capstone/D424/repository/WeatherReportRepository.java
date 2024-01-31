package com.capstone.D424.repository;

import com.capstone.D424.entities.WeatherReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface WeatherReportRepository extends JpaRepository<WeatherReport, Long> {
    @Query("SELECT w FROM WeatherReport w WHERE w.create_date < :cutoffDate")
    List<WeatherReport> findAllReportsOlderThan(Date cutoffDate);
}
