package dev.ikecruz.scraper;

import java.io.IOException;
import java.util.HashMap;

import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import dev.ikecruz.util.RandomUserAgent;

public abstract class Scraper {

    protected SessionFactory sessionFactory;

    public Document getDocument(String url, HashMap<String, String> headers) {

        Document doc;

        try {

            doc = Jsoup.connect(url)
                .userAgent(RandomUserAgent.getRandomUserAgent())
                .maxBodySize(1024*1024*3) 
                .followRedirects(true)
                .timeout(100000)
                .headers(headers)
                .get();

            

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return doc;

    }    

    public void setSessionFactory( SessionFactory sessionFactory ){
        this.sessionFactory = sessionFactory;
    }
    
    public void savePhone(String source) {
        System.out.println("Saving Phone " + source);
    }

    public abstract HashMap<String, String> getRequestHeaders();

    public abstract void scrape() throws InterruptedException;

}