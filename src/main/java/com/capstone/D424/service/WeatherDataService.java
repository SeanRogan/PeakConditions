package com.capstone.D424.service;

import java.util.List;

public interface WeatherDataService {
     List<List<String>> getWeatherData(String uri , String tempFormat);
}
