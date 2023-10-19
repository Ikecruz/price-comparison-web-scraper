package dev.ikecruz.thread;

import dev.ikecruz.database.Hibernate;
import dev.ikecruz.scraper.JohnLewisScraper;
import dev.ikecruz.scraper.Scraper;

public class ThreadStarter {
    
    private final Scraper[] scrapers = {
        new JohnLewisScraper(),
    };

    private final Hibernate hibernate;

    public ThreadStarter(Hibernate hibernate) {
        this.hibernate = hibernate;
    }

    public void scrapeAll() {
        
        // Initialize database connection before starting the scraping process.
        hibernate.init();

        // Init and start all scrappers
        for (Scraper scraper: scrapers) {

            scraper.setSessionFactory(hibernate.getSessionFactory());

            ScraperThread scraperThread = new ScraperThread(scraper);
            scraperThread.start();
        }
    }

}
