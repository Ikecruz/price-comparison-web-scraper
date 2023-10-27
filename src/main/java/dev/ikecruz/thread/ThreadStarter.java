package dev.ikecruz.thread;

import java.util.List;
import dev.ikecruz.scraper.Scraper;

public class ThreadStarter {
    
    private List<Scraper> scrapers;

    public void setScrapers (List<Scraper> scrapers) {
        this.scrapers = scrapers;
    }

    public void scrapeAll() {
        // Init and start all scrappers
        for (Scraper scraper: scrapers) {
            ScraperThread scraperThread = new ScraperThread(scraper);
            scraperThread.start();
        }
    }

}
