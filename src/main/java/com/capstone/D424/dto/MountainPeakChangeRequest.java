package com.capstone.D424.dto;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@AllArgsConstructor
@Builder
@Data
@RequiredArgsConstructor
public class MountainPeakChangeRequest {

    private Long profileId;
    private Long peakId;

}
