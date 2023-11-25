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

public class ArgosScraper extends Scraper {

    private static final String URL = "https://www.argos.co.uk/browse/technology/mobile-phones-and-accessories/sim-free-phones/c:30147/brands:samsung/?tag=ar:shop:samsung-mobiles:shop-all-header-br";

    @Override
    public void scrape() throws InterruptedException, IOException {

        FirefoxDriver driver = this.getFireFoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 2);
        WebDriverWait waitLong = new WebDriverWait(driver, 10);

        driver.get(URL);

        boolean moreData = true;

        WebElement acceptCookiesButton = waitLong.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@id='consent_prompt_submit']")
        ));

        acceptCookiesButton.click();

        List<String> phoneUrls = new ArrayList<>();

        while (moreData) {

            try {

                WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@data-test, 'component-pagination-arrow-right')]")
                ));

                List<WebElement> phonesElements = driver.findElementsByXPath(" //a[contains(@id, 'product-title')]");

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
                    By.xpath("//div[contains(@data-test, 'component-media-gallery_activeSlide-0')]//descendant::img")
                ))).getAttribute("src");

                String cellular = (driver.findElementByXPath(
                    "//div//child::ul//child::li[contains(text(), 'network capability')]"
                ).getAttribute("innerText"));

                cellular = (cellular.split(" ")[0]);

                String storage = (driver.findElementByXPath(
                    "//div//child::ul//child::li[contains(text(), 'Internal memory')]"
                ).getAttribute("innerText"));

                storage = (storage.split(" ")[2]).replaceFirst(".$","");

                String price = (driver.findElementByXPath(
                    "//li[contains(@data-test, 'product-price-primary')]"
                ).getAttribute("content"));

                // GETTING REAL NAME

                String nameFromMainSite = (driver.findElementByXPath(
                    "//p[contains(text(), 'Model number:')]"
                ).getAttribute("innerText"));
                
                nameFromMainSite = (nameFromMainSite.split(" ")[2]).replaceFirst(".$","");

                driver.navigate().to("https://www.gsmarena.com/res.php3?sSearch="+nameFromMainSite);

                WebElement phoneElementToGetRightNameFrom = driver.findElementByXPath(
                    "//div[@class='makers']//descendant::a[1]"
                );

                phoneElementToGetRightNameFrom.click();

                String phoneCorrectName = (wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h1[contains(@data-spec, 'modelname')]")
                ))).getAttribute("innerText");

                // END

                PhoneEntity phone = this.getOrCreatePhoneIfNotExist(phoneCorrectName, storage , cellular, imageUrl);

                ComparisonEntity comparison = new ComparisonEntity();
                comparison.setPhoneEntity(phone);
                comparison.setName("John Lewis");
                comparison.setPrice(
                    Float.parseFloat(price)
                );
                comparison.setUrl(phoneUrl);

                this.createAndSaveComparisonIfNotExist(comparison);

                Thread.sleep(5000);
                
            } catch (Exception e) {
                continue;
            }
            
        }

        driver.close();

    }

}
