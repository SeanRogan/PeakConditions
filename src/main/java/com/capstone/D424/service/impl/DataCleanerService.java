package com.capstone.D424.service.impl;


import com.capstone.D424.entities.WeatherReport;
import com.capstone.D424.repository.WeatherReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class DataCleanerService {

    private final WeatherReportRepository repo;
    //todo need to make a function to go through all the weather reports and delete any that are more than 24 hours old. run the function every 24 hour
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight - 00:00:00
        public void run() {
            System.out.println("running scheduled job - cleaning up database of old weather reports");
            deleteOldReports();
    }

    private void deleteOldReports() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date cutoffDate = cal.getTime();
        try{
            List<WeatherReport> oldReports = repo.findAllReportsOlderThan(cutoffDate);
            repo.deleteAll(oldReports);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
