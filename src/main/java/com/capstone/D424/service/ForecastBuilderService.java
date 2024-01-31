package com.capstone.D424.service;

import org.springframework.http.ResponseEntity;

public interface ForecastBuilderService {
    ResponseEntity<String> createWeatherReportResponse(Long peakId, int numberOfDays, String tempFormat);
}
