package com.capstone.D424.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@RequiredArgsConstructor
public class ProfileChangeRequest {
    private Long profileId;
    private int maxTemp;
    private int minTemp;
    private int maxWind;
    private boolean preferLightRain;
    private boolean preferRainShowers;
    private boolean preferModRain;
    private boolean preferRiskTstorm;
    private boolean preferLightSnow;
    private boolean preferSnowShowers;
    private boolean preferHeavySnow;
    private boolean preferClear;
    private boolean preferSomeClouds;
    private boolean preferCloudy;
}
