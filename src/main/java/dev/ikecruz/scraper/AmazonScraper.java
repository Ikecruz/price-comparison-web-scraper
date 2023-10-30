package dev.ikecruz.scraper;

import java.io.IOException;

import org.openqa.selenium.firefox.FirefoxDriver;

public class AmazonScraper extends Scraper {
    
    private static final String URL = "https://www.amazon.co.uk/s?bbn=5362060031&rh=n%3A5362060031%2Cp_89%3ASamsung&dc&qid=1697716095&rnid=1632651031&ref=lp_5362060031_nr_p_89_0";

    @Override
    public void scrape() throws InterruptedException, IOException {
        
        FirefoxDriver driver = this.getFireFoxDriver();
        driver.get(URL);

    }

}
