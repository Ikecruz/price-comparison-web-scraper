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
        options.setHeadless(true);
        System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver");

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
            "from ComparisonEntity where name='" + comparison.getName() +"' and phone_id=" + comparison.getPhoneId()
        ).getResultList();

        if (comparisons.size() > 0) {
            session.close();
            return;
        }

        session.save(comparison);
        session.getTransaction().commit();

        session.close();
    }

    /**
     * The method retrieves or creates a phone ID based on the provided name, storage, and cellular
     * information.
     * 
     * @param name The name of the phone.
     * @param storage The "storage" parameter refers to the storage capacity of the phone, such as
     * 16GB, 32GB, 64GB, etc.
     * @param cellular The "cellular" parameter refers to the type of cellular network technology
     * supported by the phone, such as 2G, 3G, 4G, or 5G.
     * @param imageUrl The imageUrl parameter is a string that represents the URL of an image for the
     * phone.
     * @return The method is returning an integer value, which is either the id of the newly created
     * phone entity if it did not exist in the database, or the id of the existing phone entity if it
     * already existed in the database.
     */
    public int getOrCreatePhoneIdIfNotExist (
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
            int modelId = this.getOrCreateModelIdIfNotExist(name, session);

            PhoneEntity phone = new PhoneEntity();
            phone.setModelId(modelId);
            phone.setImageUrl(imageUrl);
            phone.setName(name);
            phone.setStorage(storage);
            phone.setCellular(cellular);

            int id = (Integer) session.save(phone);
            session.getTransaction().commit();
            session.close();

            return id;
            
        }

        session.close();

        return phones.get(0).getId();
    }

    public int getOrCreateModelIdIfNotExist (String modelName, Session session) {
        @SuppressWarnings("unchecked")
        List<ModelEntity> models = session.createQuery("from ModelEntity where name= '" + modelName + "'").getResultList();

        if (models.size() == 0) {
            ModelEntity model = new ModelEntity();
            model.setBrand("Samsung");
            model.setName(modelName);
            
            int id = (Integer) session.save(model);

            return id;
        }

        return models.get(0).getId();
    }

    public abstract void scrape() throws InterruptedException, IOException;

}