package dev.ikecruz.thread;

import org.tinylog.Logger;
import dev.ikecruz.scraper.Scraper;

public class ScraperThread extends Thread {

    private final Scraper scraper;
    private boolean runThread;

    public ScraperThread(Scraper scraper){
        this.scraper = scraper;
    }
    
    public void run() {

        runThread = true;

        while (runThread) {

            try {
                
                scraper.scrape();
                sleep(5000);

            } catch (Exception e) {
                Logger.error(e.getMessage());
                runThread = false;
            }

        }
    }

    public void stopThread() {
        runThread = false;
    }

}
