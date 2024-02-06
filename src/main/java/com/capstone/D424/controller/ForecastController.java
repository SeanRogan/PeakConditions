package com.capstone.D424.controller;

import com.capstone.D424.service.ForecastBuilderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class handling HTTP requests for weather information. Provides endpoints for getting daily
 * and extended weather reports with the provided mountain peak id.
 */

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ForecastController {

    private final ForecastBuilderService forecastBuilderService;


    /**
     * Gets the daily forecast of the specified mountain peak. it extracts the temp-format header from the request and
     * calls the forecastBuilderService with the mountain id and temp format header.
     *
     * @param request    The HttpServlet Request received by the controller
     * @param mountainId the Mountain Peak Id number
     * @return ResponseEntity<String> returns a daily weather report in the format of a Json object
     * @see ForecastBuilderService
     */

    @GetMapping(value = "/public/report/daily/{mountainId}")
    public ResponseEntity<String> getDailyForecastByMountainId(HttpServletRequest request, @PathVariable Long mountainId) {
        log.info("creating one day weather forecast for " + mountainId);
        String tempFormatHeader = request.getHeader("Temp-format");
        if (tempFormatHeader == null || tempFormatHeader.isEmpty()) tempFormatHeader = "F";
        return forecastBuilderService.createWeatherReportResponse(mountainId, 1, tempFormatHeader);
    }

    /**
     * Gets the 6 day extended forecast for the chosen mountain from the forecastBuilderService
     *
     * @param request    The HttpServlet Request received by the controller
     * @param mountainId the Mountain Peak Id number
     * @return ResponseEntity<String> returns a six day weather report in the format of a Json object
     * @see ForecastBuilderService
     */
    @GetMapping("/report/extended/{mountainId}")
    public ResponseEntity<String> getSixDayForecastByMountainId(HttpServletRequest request, @PathVariable Long mountainId) {
        log.info("creating six day weather forecast for " + mountainId);
        String tempFormatHeader = request.getHeader("Temp-format");
        if (tempFormatHeader == null || tempFormatHeader.isEmpty()) tempFormatHeader = "F";
        return forecastBuilderService.createWeatherReportResponse(mountainId, 6, tempFormatHeader);
    }

}