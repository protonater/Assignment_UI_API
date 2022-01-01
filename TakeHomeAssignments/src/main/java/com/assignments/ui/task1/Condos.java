package com.assignments.ui.task1;

import com.assignments.ui.MyProperties;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Condos {
    private MyProperties myProperties = new MyProperties();
    private static final String CONDOS_URL = "condos.url";
    private WebDriver driver;
    private List<Element> elementList;

    public Condos() {
    }

    public static void main(String[] args) {
        Condos condos = new Condos();

        try {
            condos.gotoURL();
            condos.inputLocation();
            List<String> priceList = condos.getListOfProperties();
            condos.sortAndPrint(priceList);
            condos.select5thProperty();
            System.out.println("Halt");
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            condos.quitDriver();
        }

    }

    public void gotoURL() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        this.driver = new ChromeDriver();
        this.driver.get(this.myProperties.getProperty("condos.url"));
        TimeUnit.SECONDS.sleep(6L);
    }

    private void inputLocation() throws InterruptedException {
        this.driver.findElement(By.id("search-input")).click();
        TimeUnit.SECONDS.sleep(1L);
        this.driver.findElement(By.className("react-autosuggest__input")).sendKeys(new CharSequence[]{"toronto"});
        TimeUnit.SECONDS.sleep(1L);

        try {
            for(int i = 0; i < 4; ++i) {
                Document doc = Jsoup.parse(this.driver.findElement(By.id("react-autowhatever-1-section-0-item-" + i)).getAttribute("innerHTML"));
                if (doc.getElementsByAttributeValueContaining("class", "ResultItem").text().equalsIgnoreCase("Toronto")) {
                    this.driver.findElement(By.id("react-autowhatever-1-section-0-item-" + i)).click();
                    TimeUnit.SECONDS.sleep(4L);
                    break;
                }
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private List<String> getListOfProperties() {
        Document doc = Jsoup.parse(this.driver.findElement(By.id("listRow")).getAttribute("innerHTML"));
        this.elementList = doc.getElementsByAttributeValueContaining("class", "AskingPrice");
        List<String> priceList = new ArrayList();
        this.elementList.forEach((element) -> {
            if (element.text().contains("$")) {
                priceList.add(element.text());
            } else {
                priceList.add(element.getElementsByAttributeValueContaining("class", "BlurCont").text());
            }

        });
        return priceList;
    }

    private void sortAndPrint(List<String> priceList) {
        priceList.sort((a, b) -> Integer.parseInt(b.replace("$", "").replaceAll(",", ""))
                - Integer.parseInt(a.replace("$", "").replaceAll(",", "")));
        PrintStream var10001 = System.out;
        Objects.requireNonNull(var10001);
        priceList.forEach(var10001::println);
    }

    private void select5thProperty() {
        this.elementList.clear();
        this.elementList = Jsoup.parse(this.driver.findElement(By.id("listRow")).getAttribute("innerHTML")).getElementsByAttributeValueContaining("class", "ListingPreview");
        String tag = ((Element)((Element)this.elementList.get(5)).getElementsByTag("a").get(0)).attr("href");
        List<WebElement> elements = this.driver.findElement(By.id("listRow")).findElements(By.xpath("//div"));
        System.out.println("Halt");
    }

    private void quitDriver() {
        this.driver.close();
        this.driver.quit();
    }
}
