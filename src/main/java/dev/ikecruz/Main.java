package dev.ikecruz;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import dev.ikecruz.config.AppConfig;
import dev.ikecruz.thread.ThreadStarter;

public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(
            AppConfig.class
        );

        ThreadStarter mainThread = (ThreadStarter) context.getBean("thread");
        mainThread.scrapeAll();

    }
}