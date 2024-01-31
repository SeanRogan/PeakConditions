package com.capstone.D424.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Forecast {
    @NotNull
    @NotEmpty
    private final Map<String, Report> forecastData;
    public Forecast(Report amReport, Report pmReport, Report nightReport) {
        Map<String,Report> forecastDataMap = new LinkedHashMap<>();
        forecastDataMap.put("AM",amReport);
        forecastDataMap.put("PM",pmReport);
        forecastDataMap.put("Night",nightReport);
        this.forecastData = forecastDataMap;
    }

}

