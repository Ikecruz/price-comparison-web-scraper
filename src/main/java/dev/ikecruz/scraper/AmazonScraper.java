package dev.ikecruz.scraper;

import java.util.HashMap;

import org.jsoup.nodes.Document;

public class AmazonScraper extends Scraper {
    
    private static final String URL = "https://www.amazon.co.uk/s?bbn=5362060031&rh=n%3A5362060031%2Cp_89%3ASamsung&dc&qid=1697716095&rnid=1632651031&ref=lp_5362060031_nr_p_89_0";

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
    public void scrape() {
        Document document = this.getDocument(URL, this.getRequestHeaders()); 

        System.out.println(document);

    }

}
