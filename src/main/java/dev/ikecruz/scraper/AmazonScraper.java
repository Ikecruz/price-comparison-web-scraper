package dev.ikecruz.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.ikecruz.entities.ComparisonEntity;
import dev.ikecruz.entities.PhoneEntity;

public class AmazonScraper extends Scraper {
    
    private static final String URL = "https://www.amazon.co.uk/stores/page/01ADB8A7-48F6-4BEA-BC58-C090A763DDB5?ingress=2&visitId=9ab82e23-f025-4356-92a5-796816e052df&ref_=ast_bln";

    @Override
    public void scrape() throws InterruptedException, IOException {
        
        FirefoxDriver driver = this.getFireFoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 2);
        WebDriverWait waitLong = new WebDriverWait(driver, 10);
        
        driver.get(URL);
        waitLong.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//a[contains(@class, 'ProductGridItem__overlay__IQ3Kw')]")
        ));

        List<WebElement> phonesElements = driver.findElementsByXPath("//a[contains(@class, 'ProductGridItem__overlay__IQ3Kw')]");
        List<String> phoneUrls = new ArrayList<>();

        for (WebElement phonesElement: phonesElements) {
            phoneUrls.add(phonesElement.getAttribute("href"));
        }

        for (String phoneUrl: phoneUrls) {
            try {
                
                driver.navigate().to(phoneUrl); 

                String imageUrl = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//img[@id='landingImage']")
                )).getAttribute("src");

                String storage = (driver.findElementByXPath(
                    "//tr[contains(@class, 'po-memory_storage_capacity')]//descendant::span[2]"
                )).getAttribute("innerText");

                String cellular;

                try {
                    cellular = (driver.findElementByXPath(
                        "//tr[contains(@class, 'po-cellular_technology')]//descendant::span[2]"
                    )).getAttribute("innerText");
                } catch (NoSuchElementException e) {
                    cellular = "5G";
                }

                String price = (driver.findElementByXPath(
                    "//span[contains(@class, 'a-price-whole')]"
                )).getAttribute("innerText");

                String name = (driver.findElementByXPath(
                    "//tr[contains(@class, 'po-model_name')]//descendant::span[2]"
                ).getAttribute("innerText"));
                
                if (!name.split(" ")[0].equalsIgnoreCase("galaxy") && !name.split(" ")[0].equalsIgnoreCase("samsung")) {

                    driver.navigate().to("https://www.samsung.com/uk/search/?searchvalue="+name);

                    WebElement acceptSamsungCookiesButton = waitLong.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@id='truste-consent-button']")
                    ));

                    acceptSamsungCookiesButton.click();

                    WebElement productTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//a[contains(@an-la, 'tab:products')]")
                    ));
                    
                    productTab.click();

                    name = (wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//a[@class='result-title__link' and contains(@data-href-target, 'galaxy')]")
                    ))).getAttribute("innerText");
                }

                PhoneEntity phone = this.getOrCreatePhoneIfNotExist(name, storage, cellular, imageUrl);

                ComparisonEntity comparisonEntity = new ComparisonEntity();
                comparisonEntity.setPhoneEntity(phone);
                comparisonEntity.setName("Amazon");
                comparisonEntity.setPrice(
                    Float.parseFloat(price.replaceAll("[^0-9.]+", ""))
                );
                comparisonEntity.setUrl(phoneUrl);

                this.createAndSaveComparisonIfNotExist(comparisonEntity);

                Thread.sleep(5000);
                
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }

        }

        driver.close();

    }

}
