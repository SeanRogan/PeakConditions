package com.capstone.D424.repository;

import com.capstone.D424.entities.MountainRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MountainRangeRepository extends JpaRepository<MountainRange, Long> {
}
