package dev.ikecruz.scraper;

import java.util.HashMap;

import org.jsoup.nodes.Document;

public class JohnLewisScraper extends Scraper {
    
    private static final String URL = "https://www.johnlewis.com/browse/electricals/mobile-phones-accessories/view-all-mobile-phones/samsung/_/N-a8vZ1z13z13#intcmp=ic_20230331_mobileareapagecarouselsamsung_cp_ele_a_othr_";

    @Override
    public HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();

        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "en-US,en;q=0.5");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");
        headers.put("Sec-Fetch-Dest", "document");

        return headers;
    }

    @Override
    public void scrape() throws InterruptedException {
        
        Document document = this.getDocument(URL, this.getRequestHeaders());
        System.out.println(document);
        
        Thread.sleep(100);

    }

}
