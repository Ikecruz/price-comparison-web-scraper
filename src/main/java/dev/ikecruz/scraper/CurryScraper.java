package dev.ikecruz.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.ikecruz.entities.ComparisonEntity;
import dev.ikecruz.entities.PhoneEntity;

public class CurryScraper extends Scraper{

    private static final String URL = "https://www.currys.co.uk/phones/mobile-phones/mobile-phones/samsung";

    @Override
    public void scrape() throws InterruptedException, IOException {
        
        FirefoxDriver driver = this.getFireFoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 2);
        WebDriverWait waitLong = new WebDriverWait(driver, 10);

        driver.get(URL);

        boolean moreData = true;

        WebElement acceptCookiesButton = waitLong.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@id='onetrust-accept-btn-handler']")
        ));

        acceptCookiesButton.click();
        
        List<String> phoneUrls = new ArrayList<>();

        while (moreData) {

            try {

                List<WebElement> phonesElements = driver.findElementsByXPath("//div[contains(@class, 'add-to-cart')]/a[contains(@class, 'click-beacon')]");

                for (WebElement phoneElement: phonesElements) {
                    phoneUrls.add(phoneElement.getAttribute("href"));
                }

                WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@class='page-next']")
                ));

                nextButton.click();

            } catch (TimeoutException | NoSuchElementException e) {
                moreData = false;
            }
            
        }

        for (String phoneUrl: phoneUrls) {

            try {
                driver.navigate().to(phoneUrl);

                String imageUrl = (wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//img[@itemprop='image']")
                )).getAttribute("src"));

                // WebElement buttonToOpenSpecifications = wait.until(ExpectedConditions.presenceOfElementLocated(
                //     By.xpath("//span[contains(text(), 'Technical Specifications')]//ancestor::button")
                // ));

                // buttonToOpenSpecifications.click();

                String cellular = (driver.findElementByXPath(
                    "//div[contains(@class,  'tech-specification-th') and contains(text(), '4G network')]//following-sibling::div"
                ).getAttribute("innerText"));

                String[] cellularArray = cellular.split("/");

                cellular = cellularArray[cellularArray.length - 1];

                String storage = (driver.findElementByXPath(
                    "//div[contains(@class,  'tech-specification-th') and contains(text(), 'Internal storage')]//following-sibling::div"
                ).getAttribute("innerText"));

                String price = (driver.findElementByXPath(
                    "//div[@class='prices']//descendant::span[@class='value']"
                ).getAttribute("content"));
                
                String name = (driver.findElementByXPath(
                    "//div[contains(@class,  'tech-specification-th') and contains(text(), 'Box contents')]//following-sibling::div/text()"
                ).getText());

                System.out.println(name);

                // PhoneEntity phone = this.getOrCreatePhoneIfNotExist("Samsung "+name, storage, cellular, imageUrl);

                // ComparisonEntity comparison = new ComparisonEntity();
                // comparison.setPhoneEntity(phone);
                // comparison.setName("Back Market");
                // comparison.setPrice(
                //     Float.parseFloat(price.replaceAll("[^0-9.]+", ""))
                // );
                // comparison.setUrl(phoneUrl);

                // this.createAndSaveComparisonIfNotExist(comparison);

                Thread.sleep(5000);

            } catch (Exception e) {
                System.out.println(e);
                continue;
            }

        }

    }
    
}
