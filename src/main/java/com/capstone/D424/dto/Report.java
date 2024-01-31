package com.capstone.D424.dto;

public class Report {

    private final String dayOfTheWeek;
    private final String peakName;
    private final String high;
    private final String low;
    private final float expectedRainfall;
    private final float expectedSnowfall;
    private final String weatherConditions;
    private final String windConditions;


    private Report(ReportBuilder builder) {
        this.peakName = builder.peakName;
        this.dayOfTheWeek = builder.dayOfTheWeek;
        this.high = builder.high;
        this.low = builder.low;
        this.expectedSnowfall = builder.expectedSnowfall;
        this.expectedRainfall = builder.expectedRainfall;
        this.weatherConditions = builder.weatherConditions;
        this.windConditions = builder.windConditions;
    }

    public String getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public String getPeakName() {
        return peakName;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public float getExpectedRainfall() {
        return expectedRainfall;
    }

    public float getExpectedSnowfall() {
        return expectedSnowfall;
    }

    public String getWeatherConditions() {
        return weatherConditions;
    }

    public String getWindConditions() {
        return windConditions;
    }
    public static class ReportBuilder {

        private String dayOfTheWeek;
        private String peakName;
        private String high;
        private String low;
        private float expectedRainfall;
        private float expectedSnowfall;
        private String weatherConditions;
        private String windConditions;

        public ReportBuilder() {}


        public ReportBuilder day(String dayOfTheWeek) {
            this.dayOfTheWeek = dayOfTheWeek;
            return this;
        }

        public ReportBuilder name(String peakName) {
            this.peakName = peakName;
            return this;
        }


        public ReportBuilder high(String high) {
            this.high = high;
            return this;
        }
        public ReportBuilder low(String low) {
            this.low = low;
            return this;
        }

        public ReportBuilder rain(Float rainfall) {
            this.expectedRainfall = rainfall;
            return this;
        }
        public ReportBuilder snow(Float snowfall) {
            this.expectedSnowfall = snowfall;
            return this;
        }
        public ReportBuilder weatherConditions(String conditions) {
            this.weatherConditions = conditions;
            return this;
        }
        public ReportBuilder wind(String wind) {
            this.windConditions = wind;
            return this;
        }
        public Report build() {
            return new Report(this);
        }

    }
}
