package dev.ikecruz.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.tinylog.Logger;

public class Hibernate {
    
    private SessionFactory sessionFactory;

    public Hibernate() {    
    }

    public void init(){
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

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
