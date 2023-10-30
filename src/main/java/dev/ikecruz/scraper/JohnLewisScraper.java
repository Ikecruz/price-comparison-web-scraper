package dev.ikecruz.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.ikecruz.entities.ComparisonEntity;

public class JohnLewisScraper extends Scraper {
    private static final String PAGE_URL = "https://www.johnlewis.com/browse/electricals/mobile-phones-accessories/view-all-mobile-phones/samsung/_/N-a8vZ1z13z13#intcmp=ic_20230331_mobileareapagecarouselsamsung_cp_ele_a_othr_";

    @Override
    public void scrape() throws InterruptedException, IOException {

        FirefoxDriver driver = this.getFireFoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 30);

        driver.get(PAGE_URL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='Sort_sort__select__4_dY9']")));

        List<WebElement> phonesElements = driver.findElementsByXPath("//a[@class='product-card_c-product-card__link__QeVVQ']");
        List<String> phoneUrls = new ArrayList<>();

        for (WebElement phoneElement: phonesElements) {
            phoneUrls.add(phoneElement.getAttribute("href"));
        }

        for (String phoneUrl: phoneUrls) {
            driver.navigate().to(phoneUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h1[@class='ProductHeader_ProductHeader__title__t2mwc']")
            ));

            String imageURL = (driver.findElementByXPath(
                "//div[@class='ProductImage_ProductImage__TX23k zoom']//child::img"
            )).getAttribute("src");

            String name = (driver.findElementByXPath(
                "//span[contains(text(), 'Model name')]//ancestor::dt//following-sibling::dd[1]"
            )).getAttribute("innerText");

            String storage = (driver.findElementByXPath(
                "//span[contains(text(), 'Hard drive')]//ancestor::dt//following-sibling::dd[1]"
            )).getAttribute("innerText");

            String cellullar = (driver.findElementByXPath(
                "//span[contains(text(), 'Cellular generation')]//ancestor::dt//following-sibling::dd[1]"
            )).getAttribute("innerText");

            String price = (driver.findElementByXPath(
                "//span[@class='ProductPrice_ProductPrice__item__f6Pv6']"
            )).getAttribute("innerText");

            int phoneId = this.getOrCreatePhoneIdIfNotExist(name, storage, cellullar, imageURL);

            ComparisonEntity comparison = new ComparisonEntity();
            comparison.setPhoneId(phoneId);
            comparison.setName("John Lewis");
            comparison.setPrice(
                Float.parseFloat(price.substring(1).replaceAll("/[,]\\w+/g", ""))
            );
            comparison.setUrl(phoneUrl);

            this.createAndSaveComparisonIfNotExist(comparison);

        }

        driver.close();

    }

}
