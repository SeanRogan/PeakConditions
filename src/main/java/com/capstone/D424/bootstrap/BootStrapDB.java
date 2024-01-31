package com.capstone.D424.bootstrap;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.MountainRange;
import com.capstone.D424.entities.MountainSubRange;
import com.capstone.D424.repository.MountainPeakRepository;
import com.capstone.D424.repository.MountainRangeRepository;
import com.capstone.D424.repository.MountainSubRangeRepository;
import com.capstone.D424.service.WebScraperService;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BootStrapDB is a utility class used to set up the database on server startup..
 * it contains business logic to control web-scraping of pages,
 * populating the database with relevant information about pages on calling the init() method.
 */
@NoArgsConstructor
@Service
@Slf4j
@Transactional
public class BootStrapDB {

    final private String baseUrl = "https://www.mountain-forecast.com";
    WebScraperService ws;
    MountainPeakRepository peakRepo;
    MountainRangeRepository rangeRepo;
    MountainSubRangeRepository subRangeRepo;

    private boolean initialized;
    private List<MountainRange> allRanges;
    private List<MountainSubRange> allSubRanges;
    private List<MountainPeak> allPeaks;

    @Autowired
    public BootStrapDB(WebScraperService ws,
                       MountainPeakRepository peakRepo,
                       MountainRangeRepository rangeRepo,
                       MountainSubRangeRepository subRangeRepo) {
        this.ws = ws;
        this.peakRepo = peakRepo;
        this.rangeRepo = rangeRepo;
        this.subRangeRepo = subRangeRepo;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * the init method initializes the Database : it connects to the website, collects all major mountain range name/uri info to a Map,
     * then loops through the map to create a new entity in the Postgres database for each major range,
     * along with their associated sub-ranges, and all the mountain peaks associates with that mountain range.
     */

    public void init() {

        log.info("Collecting Data to populate DB with mountain ranges, sub ranges, and mountain peaks.");
        //get all mountain ranges, key = name of range, value = range URI
        HashMap<String, String> mountainRangeUriMap = getAllMajorMountainRangeUrls();
        //for each range in list, create new mountain range entity,
        // getAllSubRanges() and getAllPeaksInRange() should trigger
        // creation of all peaks and sub range entities.
        if (!isInitialized()) {
            mountainRangeUriMap.forEach((key, rangeLocation) -> {
                log.info("Creating Mountain Range Entity in Database: " + key);
                MountainRange range = new MountainRange(key, baseUrl + rangeLocation);
                try {
                    rangeRepo.save(range);
                } catch (Exception e) {
                    log.error(e.toString());
                    e.printStackTrace();
                }
            });
            log.info("Range data collection is complete. creating subRanges.");

            allRanges = rangeRepo.findAll();
            try {
                for (MountainRange mr : allRanges) {
                    log.info("creating subranges for major range: " + mr.getRangeName());
                    Set<MountainSubRange> subRanges;
                    allSubRanges = new ArrayList<>();
                    allPeaks = new ArrayList<>();
                    subRanges = getAllSubRanges(mr.getUri());
                    //sometimes the scraped resource uses the /peaks endpoint for subRanges,
                    // but smaller ranges use the /subRanges endpoint
                    if (subRanges.isEmpty() || subRanges == null) {
                        String uri = mr.getUri();
                        uri = uri.replace("/peaks", "/subranges");
                        subRanges = getAllSubRanges(uri);
                    }
                    log.info("subRanges created, creating mountain peaks.");
                    Set<MountainPeak> homeRangePeaks = mr.getPeaks();
                    log.info("gathering existing homeRangePeaks: ");
                    if (homeRangePeaks == null) homeRangePeaks = new HashSet<>();
                    for (MountainSubRange sr : subRanges) {
                        log.info("collecting mountain peaks for subrange :" + sr.getRangeName());
                        Set<MountainPeak> peaks = new HashSet<>();
                        allSubRanges.add(sr);
                        //scrape the subrange page and gather all the peaks
                        Document subRangePage = ws.scrapeDocument(sr.getUri());
                        Elements peakLinkElements = subRangePage.getElementsByClass("b-list-table__item-name")
                                .select("a[href]");
                        //loop thru each peak link element
                        for (Element e : peakLinkElements) {
                            log.info("creating new mountain peak: " + e.text()
                                    + " , adding to set of peaks for subrange: " + sr.getRangeName());
                            //add new peak entity to set
                            peaks.add(new MountainPeak(e.text(), baseUrl + e.attr("href")));
                        }
                        //iterate through all peaks to associate their homesubrange and homerange.

                        for (MountainPeak p : peaks) {
                            log.info("setting home ranges for mountain peak: " + p.getPeakName());
                            p.setHomeSubrange(sr);
                            p.setHomeRange(mr);
                            homeRangePeaks.add(p);
                            log.info("saving peak to repo as : " + p
                                    + ", homeRange: " + p.getHomeRange()
                                    + ", homeSubRange: " + p.getHomeSubrange());
                            allPeaks.add(p);
                            peakRepo.save(p);
                        }
                        sr.setHomeRange(mr);
                        sr.setPeaks(peaks);
                        log.info("saving subrange " + sr.getRangeName() + "to repo as : " + sr
                                + ", homeRange" + sr.getHomeRange()
                                + ", peaks: " + sr.getPeaks());
                        subRangeRepo.save(sr);
                    }
                    mr.setPeaks(homeRangePeaks);
                    mr.setSubRanges(subRanges);
                    log.info("saving mountain range " + mr.getRangeName() + " to repo as : " + mr
                            + ", subRanges: " + mr.getSubRanges()
                            + ", peaks: " + mr.getPeaks());

                    rangeRepo.save(mr);
                }
            } catch (Exception e) {
                log.error(e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * @param uri the uri of the Major Range to be searched for sub-ranges.
     * @return Set<Subrange> subRanges - returns a set of Subrange objects associates with the Major Range
     */
    public Set<MountainSubRange> getAllSubRanges(String uri) {
        String homerangeUrl = uri;
        Set<MountainSubRange> subRanges = new HashSet<>();
        Document homeRange = ws.scrapeDocument(homerangeUrl);
        Elements elements = homeRange
                .getElementsByClass("b-list-table__item-name--ranges")
                .select("a[href]");
        for (Element e : elements) {
            //todo this constructor needs to associate a Set<MountainPeak> object and a MountainRange object to the subrange
            subRanges.add(new MountainSubRange(e.text(), baseUrl + e.attr("href"), homerangeUrl));
        }
        return subRanges;
    }

    /**
     * @return HashMap<String, String> listOfPeakUrls -  returns a Hashmap<String,String> of Mountain Range names with their associated URL
     */
    public HashMap<String, String> getAllMajorMountainRangeUrls() {
        HashMap<String, String> rangeUrls = new HashMap<>();
        String uri = baseUrl + "/mountain_ranges";
        Document allRanges = ws.scrapeDocument(uri);
        Elements elements = allRanges
                .getElementsByClass("b-list-table__item-name--ranges")
                .select("a[href]");
        for (Element e : elements) {
            //saves each range key = name string , value = url string
            rangeUrls.put(e.text(), e.attr("href"));
            //e.remove();
        }
        return rangeUrls;
    }
}
