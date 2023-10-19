package dev.ikecruz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.ikecruz.database.Hibernate;
import dev.ikecruz.thread.ThreadStarter;

@Configuration
public class AppConfig {

    @Bean
    public ThreadStarter thread() {
        ThreadStarter tmpThread = new ThreadStarter(hibernate());
        return tmpThread;
    }
    
    @Bean
    public Hibernate hibernate() {
        Hibernate tmpHibernate = new Hibernate();
        return tmpHibernate;
    }

}