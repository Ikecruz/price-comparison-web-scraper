package dev.ikecruz.scraper;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ArgosScraper extends Scraper {

    private static final String URL = "https://www.argos.co.uk/browse/technology/mobile-phones-and-accessories/sim-free-phones/c:30147/brands:samsung/?tag=ar:shop:samsung-mobiles:shop-all-header-br";

    @Override
    public void scrape() throws InterruptedException, IOException {

        FirefoxDriver driver = this.getFireFoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 2);

        driver.get(URL);

        boolean moreData = true;

        WebElement acceptCookiesButton = wait.until(ExpectedConditions.elementToBeClickable(
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
            driver.navigate().to(phoneUrl);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(@data-test, 'product-title')]")
            ));

            String imageUrl = (driver.findElementByXPath(
                "//div[contains(@data-test, 'component-media-gallery_activeSlide-0')]//descendant::img"
            ).getAttribute("src"));

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
            
        }

        driver.close();

    }

}
