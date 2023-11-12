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
        WebDriverWait wait = new WebDriverWait(driver, 30);
        
        driver.get(URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@class='Header__searchArea__yVqw6']")
        ));

        List<WebElement> phonesElements = driver.findElementsByXPath("//a[contains(@class, 'ProductGridItem__overlay__IQ3Kw')]");
        List<String> phoneUrls = new ArrayList<>();

        for (WebElement phonesElement: phonesElements) {
            phoneUrls.add(phonesElement.getAttribute("href"));
        }

        for (String phoneUrl: phoneUrls) {
            driver.navigate().to(phoneUrl); 
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[@id='productTitle']")
            ));

            String imageUrl = (driver.findElementByXPath(
                "//img[@id='landingImage']"
            ).getAttribute("src"));

            String nameFromMainSite = (driver.findElementByXPath(
                "//tr[contains(@class, 'po-model_name')]//descendant::span[2]"
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

            String storage = (driver.findElementByXPath(
                "//div[@id='variation_size_name']//descendant::span[@class='selection']"
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

            PhoneEntity phone = this.getOrCreatePhoneIfNotExist(phoneCorrectName, storage, cellular, imageUrl);

            ComparisonEntity comparisonEntity = new ComparisonEntity();
            comparisonEntity.setPhoneEntity(phone);
            comparisonEntity.setName("Amazon");
            comparisonEntity.setPrice(
                Float.parseFloat(price.replaceAll("[^0-9.]+", ""))
            );
            comparisonEntity.setUrl(phoneUrl);

            this.createAndSaveComparisonIfNotExist(comparisonEntity);

            Thread.sleep(5000);

        }

        driver.close();

    }

}
