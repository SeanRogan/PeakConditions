package com.capstone.D424.service.impl;

import com.capstone.D424.service.WeatherDataService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * WeatherDataService is a service class.
 * it contains business logic to control web-scraping of pages,
 * populating the database with relevant information about pages on server start-up,
 * and collect weather data from those pages.
 */
@Service
@Slf4j
public class WeatherDataServiceImpl implements WeatherDataService {
    private final WebScraperServiceImpl webScraperServiceImpl;

    @Autowired
    public WeatherDataServiceImpl(WebScraperServiceImpl webScraperServiceImpl) {
        this.webScraperServiceImpl = webScraperServiceImpl;
    }


    /**
     * @param uri the uri of the specific mountain peak with which the weather data is associated.
     * @param tempFormat the temperature units the weather report is to be displayed in; Fahrenheit or Celsius
     * @return List<String[]> dataList - returns a list of string arrays,
     * each array containing all the values of one of 7 categories,
     * categories being the high temperatures, low temperatures,
     * and windchill temps, along with wind conditions, weather summary,
     * and rainfall / snowfall estimates.
     *
     *
     * The getWeatherData method collects the weather data needed for a Report object response.
     *  if the request was sent with http headers Temp-format : F,
     *  the temperature values will be converted to imperial units
     *  via the National Institute of Standards and Technology formula : °F = (°C × 1.8) + 32
     */
    public List<List<String>> getWeatherData(String uri , String tempFormat) {

        List<List<String>> dataList = new ArrayList<>(6);
        //html tags to pull information from the webpage
        String weatherConditionsRow = "forecast-table__phrase";
        String maxTempRow = "forecast-table__container--max";
        String minTempRow = "forecast-table__container--min";
        String rainFallRow = "forecast-table__container--rain";
        String snowFallRow = "forecast-table__container--snow";
        String windRow = "forecast-table__container--wind";
        String dayAndDate = "forecast-table-days__content";
        log.info("scraping weather data from: " + uri);
        //attempt to scrape webpage
        Document doc = webScraperServiceImpl.scrapeDocument(uri);
        //if the document comes back from the datascraper, begin to pull info from it and collect it in a List
        if(doc != null) {
            Elements maxTempElements = doc.getElementsByClass(maxTempRow);
            dataList.add(0,collectToList(maxTempElements.iterator()));
            //get low temps
            Elements minTempElements = doc.getElementsByClass(minTempRow);
            dataList.add(1,collectToList(minTempElements.iterator()));

            try{
                if(tempFormat.equals("F")){
                    log.info("converting temperature values to Imperial Units");
                    List<String> convertedMax =  convertTempsToImperial(dataList.get(0));
                    dataList.remove(0);
                    dataList.add(0, convertedMax);
                    List<String> convertedMin =  convertTempsToImperial(dataList.get(1));
                    dataList.remove(1);
                    dataList.add(1, convertedMin);
                }
            } catch (NullPointerException e) {
                log.warn(e.getMessage() + ": \n A NullPointer Exception was thrown because there was no valid Temp-format header value provided");
            }


            //get snowfall
            Elements snowFallElements = doc.getElementsByClass(snowFallRow)
                    .select("div.snow-amount").select("span");
            dataList.add(2, collectToList(snowFallElements.iterator()));
            //get rainfall
            Elements rainFallElements = doc.getElementsByClass(rainFallRow)
                    .select("span");
            dataList.add(3, collectToList(rainFallElements.iterator()));

            //get weather elements
            Elements weatherConditionElements = doc.getElementsByClass(weatherConditionsRow);
            dataList.add(4, collectToList(weatherConditionElements.iterator()));

            //get wind elements
            Elements windElements = doc.getElementsByClass(windRow);
            dataList.add(5, getWindConditions(windElements.select("div.wind-icon").iterator()));

            //get days of the week
            Elements dateElements = doc.getElementsByClass(dayAndDate).select("div > div:eq(1)");
            Elements dayOfWeekElements = doc.getElementsByClass(dayAndDate).select("div > div:eq(0)");
            dataList.add(6, getDayAndDateElements(dateElements, dayOfWeekElements));
        }
        return dataList;
    }
    //pulls element texts from Elements collections and creates lists from the texts.
    private List<String> getDayAndDateElements(Elements dateElements, Elements dayOfWeekElements) {
        List<String> daysOfTheWeek = new ArrayList<>();
        List<String> daysOfTheMonth = new ArrayList<>();

        dateElements.forEach(i -> daysOfTheMonth.add(i.text()));
        dayOfWeekElements.forEach(i -> daysOfTheWeek.add(i.text()));
        List<String> results = new ArrayList<>(6);
        for(int i = 0; i < 6; i++) {
            try{
            results.add(String.format("%s (%s)",daysOfTheWeek.get(i),daysOfTheMonth.get(i)));
            } catch (Exception e ) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return results;
    }
    //converts metric temperature values to imperial units
    private List<String> convertTempsToImperial(List<String> temps) {
        List<String> convertedTemps = new ArrayList<>();
        try {
            temps.forEach(i -> {
                double n = Double.parseDouble(i);
                n = n * 1.8 + 32;
                convertedTemps.add(String.valueOf(Math.round(n)));
            });
        } catch (NullPointerException e) {
            log.warn("Null Pointer Exception thrown @ WeatherDataService.convertTempToImperial - " + e.getMessage());

        }
        return convertedTemps;
    }

    private List<String> collectToList(Iterator<Element> itr) {
        List<String> result = new ArrayList<>();

        while(itr.hasNext()) {
            Element current = itr.next();
            result.add(current.text());
        }

        return result;
    }

    //collects wind speed and direction from Elements and combines the values into a single 'wind conditions' field
    private List<String> getWindConditions(Iterator<Element> itr) {

        List<String> result = new ArrayList<>();
        while(itr.hasNext()) {
            Element current = itr.next();
            result.add(current.select("text.wind-icon__val").text()
                    + " "
                    + current.select("div.wind-icon__tooltip").text());
        }

        return result;
    }
}

