package dev.ikecruz.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.ikecruz.entities.ComparisonEntity;
import dev.ikecruz.entities.PhoneEntity;

public class BackmarketScraper extends Scraper{

    private static final String URL = "https://www.backmarket.co.uk/en-gb/l/samsung-smartphone/99760870-ed75-482f-a626-2b4f964c55ae";

    @Override
    public void scrape() throws InterruptedException, IOException {
        
        FirefoxDriver driver = this.getFireFoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 2);
        WebDriverWait waitLong = new WebDriverWait(driver, 10);

        driver.get(URL);

        boolean moreData = true;

        WebElement acceptCookiesButton = waitLong.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@data-qa='accept-cta']")
        ));

        acceptCookiesButton.click();
        
        List<String> phoneUrls = new ArrayList<>();

        while (moreData) {

            try {

                WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@aria-current, 'true')]//following-sibling::a[1]")
                ));

                List<WebElement> phonesElements = driver.findElementsByXPath("//div[@class='productCard']//a");

                for (WebElement phoneElement: phonesElements) {
                    phoneUrls.add(phoneElement.getAttribute("href"));
                }

                nextButton.click();

            } catch (TimeoutException | NoSuchElementException e) {
                moreData = false;
            }
            
        }

        for (String phoneUrl: phoneUrls) {

            try {
                driver.navigate().to(phoneUrl);

                String imageUrl = (wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@data-test='carousel']//descendant::li[1]/img")
                )).getAttribute("src"));

                WebElement buttonToOpenSpecifications = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//span[contains(text(), 'Technical Specifications')]//ancestor::button")
                ));

                buttonToOpenSpecifications.click();

                String cellular = (driver.findElementByXPath(
                    "//p[contains(text(),  'Network')]//parent::span//parent::div//following-sibling::div/span/span"
                ).getAttribute("innerText"));

                String storage = (driver.findElementByXPath(
                    "//li[contains(@data-qa, 'storage')]/a[contains(@class, 'primary-active')]//descendant::span"
                ).getAttribute("innerText"));

                String price = (driver.findElementByXPath(
                    "//div[contains(@data-test, 'normal-price')]"
                ).getAttribute("innerText"));
                
                String name = (driver.findElementByXPath(
                    "//p[contains(text(),  'Model')]//parent::span//parent::div//following-sibling::div/span/span"
                ).getAttribute("innerText"));

                PhoneEntity phone = this.getOrCreatePhoneIfNotExist("Samsung "+name, storage, cellular, imageUrl);

                ComparisonEntity comparison = new ComparisonEntity();
                comparison.setPhoneEntity(phone);
                comparison.setName("Back Market");
                comparison.setPrice(
                    Float.parseFloat(price.replaceAll("[^0-9.]+", ""))
                );
                comparison.setUrl(phoneUrl);

                this.createAndSaveComparisonIfNotExist(comparison);

                Thread.sleep(5000);

            } catch (Exception e) {
                continue;
            }

        }

    }
    
}
