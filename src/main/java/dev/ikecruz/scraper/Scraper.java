package dev.ikecruz.scraper;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import dev.ikecruz.entities.ComparisonEntity;
import dev.ikecruz.entities.ModelEntity;
import dev.ikecruz.entities.PhoneEntity;

public abstract class Scraper {

    protected SessionFactory sessionFactory;

    /**
     * The method sets the session factory for the current object.
     * 
     * @param sessionFactory The sessionFactory parameter is an object of type SessionFactory. It is
     * used to create and manage sessions in Hibernate, which is an object-relational mapping (ORM)
     * framework for Java. The sessionFactory is responsible for creating database connections,
     * managing transactions, and executing database operations.
     */
    public void setSessionFactory( SessionFactory sessionFactory ){
        this.sessionFactory = sessionFactory;
    }

    /**
     * The method returns a FirefoxDriver with headless mode enabled and the geckodriver path set.
     * 
     * @return The method is returning an instance of the FirefoxDriver class.
     */
    public FirefoxDriver getFireFoxDriver(){
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(false);
        System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "null");
        
        return new FirefoxDriver(options);
    }

    /**
     * The method creates and saves a ComparisonEntity object in the database if it does not already
     * exist.
     * 
     * @param comparison The parameter "comparison" is an instance of the ComparisonEntity class. It
     * represents the comparison that needs to be created and saved if it does not already exist in the
     * database.
     */
    public void createAndSaveComparisonIfNotExist(ComparisonEntity comparison) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        @SuppressWarnings("unchecked")
        List<ComparisonEntity> comparisons = session.createQuery(
            "from ComparisonEntity where name='" + comparison.getName() +"' and phone_id=" + comparison.getPhoneEntity().getId()
        ).getResultList();

        if (comparisons.size() > 0) {
            session.close();
            return;
        }

        session.save(comparison);
        session.getTransaction().commit();

        session.close();
    }

    public PhoneEntity getOrCreatePhoneIfNotExist (
        String name,
        String storage,
        String cellular,
        String imageUrl
    ) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        @SuppressWarnings("unchecked")
        List<PhoneEntity> phones = session.createQuery(
            "from PhoneEntity where name='" + name + "' and storage='" + storage + "' and cellular='" + cellular + "'"
        ).getResultList();

        if (phones.size() == 0) {
            ModelEntity model = this.getOrCreateModelIfNotExist(name, session);

            PhoneEntity phone = new PhoneEntity();
            phone.setModelEntity(model);
            phone.setImageUrl(imageUrl);
            phone.setName(name);
            phone.setStorage(storage);
            phone.setCellular(cellular);

            int id = (Integer) session.save(phone);
            phone.setId(id);

            session.getTransaction().commit();
            session.close();

            return phone;
            
        }

        session.close();

        return phones.get(0);
    }

    private ModelEntity getOrCreateModelIfNotExist (String modelName, Session session) {
        @SuppressWarnings("unchecked")
        List<ModelEntity> models = session.createQuery("from ModelEntity where name= '" + modelName + "'").getResultList();

        if (models.size() == 0) {
            ModelEntity model = new ModelEntity();
            model.setBrand("Samsung");
            model.setName(modelName);
            
            int id = (Integer) session.save(model);
            model.setId(id);

            return model;
        }

        return models.get(0);
    }

    public abstract void scrape() throws InterruptedException, IOException;

}