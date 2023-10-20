package dev.ikecruz.scraper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import dev.ikecruz.entities.ComparisonEntity;
import dev.ikecruz.entities.ModelEntity;
import dev.ikecruz.entities.PhoneEntity;
import dev.ikecruz.util.RandomUserAgent;

public abstract class Scraper {

    protected SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory ){
        this.sessionFactory = sessionFactory;
    }

    /**
     * The function retrieves a document from a given URL using Jsoup library in Java, with the option
     * to provide custom headers.
     * 
     * @param url The URL of the webpage you want to retrieve the document from.
     * @param headers The "headers" parameter is a HashMap that contains key-value pairs of HTTP
     * headers to be included in the request. 
     * @return The method is returning a Document object.
     */
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

    /**
     * The function creates and saves a ComparisonEntity object in the database if it does not already
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
            "from comparisons where name=" + comparison.getName()
        ).getResultList();

        if (comparisons.size() > 0) {
            return;
        }

        session.save(comparison);
        session.getTransaction().commit();

        session.close();
    }

    /**
     * The function retrieves an existing phone entity from the database based on its name, storage,
     * and cellular properties, or creates a new phone entity if it does not exist.
     * 
     * @param phone The parameter "phone" is an instance of the PhoneEntity class, which represents a
     * phone object. It contains properties such as name, storage, and cellular.
     * @return The method is returning an integer value. If a phone with the specified name, storage,
     * and cellular properties does not exist in the database, it will create a new phone entity and
     * return its ID. If a matching phone already exists, it will return the ID of the first phone in
     * the list of matching phones.
     */
    public int getOrCreatePhoneIfNotExist (PhoneEntity phone) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        @SuppressWarnings("unchecked")
        List<PhoneEntity> phones = session.createQuery(
            "from phones where name=" + phone.getName() + "and storage=" + phone.getStorage() + "and cellular=" + phone.getCellular()
        ).getResultList();

        if (phones.size() == 0) {
            int modelId = this.getOrCreateModelIfNotExist(phone.getName());
            phone.setModelId(modelId);

            int id = (Integer) session.save(phone);
            session.getTransaction().commit();
            session.close();

            return id;
            
        }

        session.close();

        return phones.get(0).getId();
    }

    /**
     * The function retrieves an existing model entity from the database or creates a new one if it
     * doesn't exist.
     * 
     * @param modelName The modelName parameter is a String that represents the name of the model.
     * @return The method is returning an integer value. If a model with the given modelName does not
     * exist, it creates a new model and returns its id. If a model with the given modelName already
     * exists, it returns the id of the existing model.
     */
    public int getOrCreateModelIfNotExist (String modelName) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        @SuppressWarnings("unchecked")
        List<ModelEntity> models = session.createQuery("from models where name=" + modelName).getResultList();

        if (models.size() == 0) {
            ModelEntity model = new ModelEntity();
            model.setBrand("Samsung");
            model.setName(modelName);
            
            int id = (Integer) session.save(model);
            session.getTransaction().commit();
            session.close();

            return id;
        }

        session.close();

        return models.get(0).getId();
    }

    public abstract HashMap<String, String> getRequestHeaders();

    public abstract void scrape() throws InterruptedException, IOException;

}