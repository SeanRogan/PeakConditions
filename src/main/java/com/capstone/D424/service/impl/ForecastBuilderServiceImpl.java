package com.capstone.D424.service.impl;

import com.capstone.D424.dto.Forecast;
import com.capstone.D424.dto.Report;
import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.WeatherReport;
import com.capstone.D424.repository.MountainPeakRepository;
import com.capstone.D424.repository.WeatherReportRepository;
import com.capstone.D424.service.ForecastBuilderService;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * The ForecastBuilderService class is a service class, it contains the business logic responsible
 * for building weather forecasts and returning a ResponseEntity<String> object to the Controller class
 */
@AllArgsConstructor
@Service
@Slf4j
@Transactional
public class ForecastBuilderServiceImpl implements ForecastBuilderService {
    private final MountainPeakRepository peakRepo;
    private final WeatherReportRepository weatherReportRepo;
    private final WeatherDataServiceImpl weatherDataServiceImpl;
    private final Gson gson;


//
//
//    @Autowired
//    public ForecastBuilderServiceImpl(MountainPeakRepository peakRepo, WeatherDataServiceImpl weatherDataServiceImpl) {
//        this.peakRepo = peakRepo;
//        this.weatherDataServiceImpl = weatherDataServiceImpl;
//        this.gson = new Gson();
//
//    }

    /**
     * createWeatherReportResponse method takes a Long number as peakId ,
     * an integer 'numberOfDays' representing a 1 or 6 day report being requested,
     * and string which was the temp-format header of the request.
     * @param peakId
     * @param numberOfDays
     * @param tempFormat
     * @return Returns a responseEntity<String> object with the forecast requested, or an error message if something went wrong in the service class.
     */


    public ResponseEntity<String> createWeatherReportResponse(Long peakId, int numberOfDays, String tempFormat) {
        String responseBody = "";
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        MountainPeak peak = null;
        WeatherReport report = null;
        List<Forecast> newWeatherForecast = null;
        //validate peak id by attempting a  lookup, if the peak object is null after lookup, the peak isnt in the database.
        //get the peak object
        Optional<MountainPeak> mountainPeak = peakRepo.getPeakByPeakId(peakId);
        if(mountainPeak.isPresent()) peak = mountainPeak.get();
        //if id check is not null, check for weather report to return and if not found create weather report, else return an error message
        if (peak != null) {
            //get weather reports associated with peak
            Set<WeatherReport> reportsAssociatedWithPeak = peak.getWeatherReports();
            log.info("checking  db for reports associated with peakId: " + peak.getPeakId() + ", peak name: " + peak.getPeakName());
            if (!reportsAssociatedWithPeak.isEmpty()) {
                //create a date representing today to check against report creation dates
                Date today = new Date();
                //normalize date for comparison
                Date normalizedToday = normalizeDate(today);
                //iterate through set and find a report created today, if found save to report object
                for (WeatherReport r : reportsAssociatedWithPeak) {
                    //check date matches today
                    if (normalizeDate(r.getCreate_date()).equals(normalizedToday)) {
                        //check that number of days in request matches cached report
                        if((numberOfDays == 1 && !r.isExtendedReport()) || numberOfDays == 6 && r.isExtendedReport()) report = r;
                    }
                }
            }
            //if no report was found, create a new report
            if (report == null) {
                log.info("report not found; creating new weather forecast for peak: " + peak.getPeakName() + ", Id: " + peak.getPeakId());
                //collect weather data
                newWeatherForecast = buildListOfForecasts(peakId,
                        weatherDataServiceImpl
                                .getWeatherData(peak.getUri(),
                                        tempFormat),
                        numberOfDays);
                //create new report object
                report = new WeatherReport();
                //set the type of report, daily or extended
                if(numberOfDays > 1) {
                    report.setExtendedReport(true);
                }
           } else {
                log.info("Existing report found for today's date for peak :" + peak.getPeakName() + ", Id: " + peak.getPeakId());
                responseBody = report.getWeatherReportContent();
            }
            //if a new forecast was created, set it as the response body
            if(newWeatherForecast != null) {
                log.info("using newly created forecast as response body");
                responseBody = gson.toJson(newWeatherForecast); //convert newWeatherForecast to Json object
                //set report content
                report.setWeatherReportContent(responseBody);
            }
            //associate peak obj to report obj
            report.setMountainPeak(peak);
            //add report to set
            reportsAssociatedWithPeak.add(report);
            weatherReportRepo.save(report);
            //save report set to peak object
            peak.setWeatherReports(reportsAssociatedWithPeak);
            //save peak and report to their repositories
            peakRepo.save(peak);
            //create the server response
            response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
            log.info("created responseEntity : \n" + response + "\n");
            return response;
        }
        //if the application gets this far the idCheck must have been null, so send an error response.
        responseBody = "Sorry, but that peak id is not associated with any mountain. Please double check the peak id is correct and try again.";
        response = new ResponseEntity<>(responseBody, headers, HttpStatus.NOT_FOUND);
        return response;
    }

    /**
     * @param peakId the id of the mountain peak.
     * @param weatherData a list of lists of strings,
     *                    each list containing a category of data needed by the forecast service;
     *                    high, low, windchill, rain, snow, wind, weather summary,
     *                    and the day of the week and date of the forecast.
     * @param num the number of days the forecast should contain. normally 1 or 6.
     *
     * the build forecast method creates 3 Report objects from the scraped weather data passed to it.
     *            It then returns a Forecast object containing the 3 reports.
     *
     * @return Forecast object containing 3 reports, AM, PM and NIGHT,
     * for the mountain specified by the peak id argument.
     */
    private Forecast buildForecast(Long peakId, List<List<String>> weatherData , int num) {
        String peakName = peakRepo.getPeakNameByPeakId(peakId);
        //max temps, min, chill, snow, rain weather, wind
        List<String> maxTemps = weatherData.get(0);
        List<String> minTemps = weatherData.get(1);
        List<String> snowForecast = weatherData.get(2);
        List<String> rainForecast = weatherData.get(3);
        List<String> weatherSummary = weatherData.get(4);
        List<String> windCondition = weatherData.get(5);
        List<String> dayAndDate = weatherData.get(6);
        log.trace("replacing: - with: 0.0 in precipitation forecasts");
        Collections.replaceAll(snowForecast, "—","0.0");
        Collections.replaceAll(snowForecast, "","0.0");
        Collections.replaceAll(rainForecast, "—","0.0");
        Collections.replaceAll(rainForecast, "","0.0");
        log.trace("creating AM report for day" + num);
        Report amReport = new Report.ReportBuilder()
                .name(peakName)
                .day(dayAndDate.get(num))
                .high(maxTemps.get(num))
                .low(minTemps.get(num))
                .snow(Float.parseFloat(snowForecast.get(num)))
                .rain(Float.parseFloat(rainForecast.get(num)))
                .weatherConditions(weatherSummary.get(num))
                .wind(windCondition.get(num)).build();
        log.trace("creating PM report for day" + num);
        Report pmReport = new Report.ReportBuilder()
                .name(peakName)
                .day(dayAndDate.get(num))
                .high(maxTemps.get(num+1))
                .low(minTemps.get(num+1))
                .snow(Float.parseFloat(snowForecast.get(num+1)))
                .rain(Float.parseFloat(rainForecast.get(num+1)))
                .weatherConditions(weatherSummary.get(num+1))
                .wind(windCondition.get(num+1)).build();
        log.trace("creating NIGHT report for day" + num);
        Report nightReport = new Report.ReportBuilder()
                .name(peakName)
                .day(dayAndDate.get(num))
                .high(maxTemps.get(num+2))
                .low(minTemps.get(num+2))
                .snow(Float.parseFloat(snowForecast.get(num+2)))
                .rain(Float.parseFloat(rainForecast.get(num+2)))
                .weatherConditions(weatherSummary.get(num+2))
                .wind(windCondition.get(num+2)).build();
        log.trace("returning Forecast");
        return new Forecast(amReport,pmReport,nightReport);

    }
    /**
     * @param peakId - the peak id number
     * @param weatherData - A List containing Lists of Strings,
     *                   each inner List containing one of the
     *                    collected weather data categories(high,low,windchill temps, etc)
     * @param days - the number of days the weather forecast should cover(normally one day or six days)\
     *
     * @return List<Forecast> a list of forecasts,one for each day specifies,
     * each forecast containing 3 weather reports for the day.
     */
    private List<Forecast> buildListOfForecasts(Long peakId, List<List<String>> weatherData, int days) {
        List<Forecast> forecastList = new ArrayList<>();
        for(int i = 0; i < days; i++) {
            forecastList.add(buildForecast(peakId, weatherData, i));
        }
        return forecastList;
    }

    private Date normalizeDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }



}
