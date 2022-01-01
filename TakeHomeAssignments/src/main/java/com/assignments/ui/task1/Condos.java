package com.assignments.ui.task1;

import com.assignments.ui.MyProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            condos.getRoomDetails();
            System.out.println("Halt");
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            condos.quitDriver();
        }

    }

    // Step 1: Go to the url - condos.ca
    public void gotoURL() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        this.driver = new ChromeDriver();
        this.driver.get(this.myProperties.getProperty(CONDOS_URL));
        TimeUnit.SECONDS.sleep(6L);
    }

    // Step 2: Input location Toronto and select Toronto from Areas
    private void inputLocation() throws InterruptedException {
        this.driver.findElement(By.id("search-input")).click();
        TimeUnit.SECONDS.sleep(1L);
        this.driver.findElement(By.className("react-autosuggest__input")).sendKeys("toronto");
        TimeUnit.SECONDS.sleep(1L);

        try {
            for (int i = 0; i < 4; ++i) {
                Document doc = Jsoup.parse(this.driver.findElement(By.id("react-autowhatever-1-section-0-item-" + i)).getAttribute("innerHTML"));
                if (doc.getElementsByAttributeValueContaining("class", "ResultItem").text().equalsIgnoreCase("Toronto")) {
                    this.driver.findElement(By.id("react-autowhatever-1-section-0-item-" + i)).click();
                    TimeUnit.SECONDS.sleep(10L);
                    break;
                }
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    // Step 3: You will get a list of properties on that page
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

    // Step 4: Print all the price values in sorted order (descending order) on the console from the first page
    private void sortAndPrint(List<String> priceList) {
        priceList.sort(Comparator.comparingInt(a -> Integer.parseInt(a.replace("$", "").replaceAll(",", ""))));
        priceList.forEach(System.out::println);
    }

    // Step 5: Select the 5th Property on that list (it's changing every minute, so it's dynamic) and go to details
    private void select5thProperty() throws InterruptedException {
        this.driver.findElement(By.xpath("//div[@id=\"listRow\"]/div[5]")).click();
        TimeUnit.SECONDS.sleep(10L);
    }

    // Step 6: From the property details page, get all rooms info (name, size and features), store them on a json
    // file and set the filename with the property address(street name);
    private void getRoomDetails() throws InterruptedException, FileNotFoundException {
        JSONArray jsonArray = new JSONArray();
        //Switch to current selected tab's content.
        ArrayList<String> newTb = new ArrayList<>(this.driver.getWindowHandles());
        this.driver.switchTo().window(newTb.get(1));

        // Get the HTML parser
        elementList.clear();
        Document doc = Jsoup.parse(this.driver.findElement(By.xpath("//article")).getAttribute("innerHTML"));
        this.driver.findElement(By.className(doc.getElementsByAttributeValueContaining("class", "ShowMore").attr("class").split(" ")[0])).click();
        TimeUnit.SECONDS.sleep(2L);
        doc.selectXpath("//*/table/tbody/tr").forEach(element -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", element.selectXpath("//tr/td[1]/div").text());
            jsonObject.put("size", element.selectXpath("//tr/td[2]/div/div/span").text());
            jsonObject.put("features", element.selectXpath("//tr/td[3]/div").text());
            jsonArray.put(jsonObject);
        });
        PrintWriter printWriter = new PrintWriter(this.driver.getTitle().split("[|]")[0]+".json");

        printWriter.write(jsonArray.toString());
        printWriter.close();
    }

    // close and quit
    private void quitDriver() {
        this.driver.close();
        this.driver.quit();
    }
}
