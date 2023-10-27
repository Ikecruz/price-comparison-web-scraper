package dev.ikecruz.config;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tinylog.Logger;

import dev.ikecruz.scraper.JohnLewisScraper;
import dev.ikecruz.scraper.Scraper;
import dev.ikecruz.thread.ThreadStarter;

@Configuration
public class AppConfig {

    public SessionFactory sessionFactory;

    @Bean
    public ThreadStarter thread() {
        ThreadStarter tmpThread = new ThreadStarter();
        List<Scraper> scrapers = new ArrayList<Scraper>();
        scrapers.add(johnLewisScraper());
        tmpThread.setScrapers(scrapers);
        return tmpThread;
    }

    @Bean
    public JohnLewisScraper johnLewisScraper() {
        JohnLewisScraper tmpJohnLewisScraper = new JohnLewisScraper();
        tmpJohnLewisScraper.setSessionFactory(getSessionFactory());
        return tmpJohnLewisScraper;
    }

    @Bean
    public SessionFactory getSessionFactory () {
        if (sessionFactory == null) {
            try {
            
                StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
                standardServiceRegistryBuilder.configure("hibernate-cfg.xml");
                StandardServiceRegistry registry = standardServiceRegistryBuilder.build();

                try {
                    sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
                } catch (Exception e) {
                    System.err.println("Session Factory build failed.");
                    e.printStackTrace();
                    StandardServiceRegistryBuilder.destroy(registry);
                }

                Logger.info("Session factory built.");
                
            } catch (Throwable ex) {
                System.err.println("SessionFactory creation failed." + ex);
            }
        }

        return sessionFactory;
    }

}