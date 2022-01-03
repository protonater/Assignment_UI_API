package com.assignments.ui.task2;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SteamPowered {
    private WebDriver webDriver;

    public static void main(String[] args) {
        SteamPowered steamPowered = new SteamPowered();
        try {
            steamPowered.gotoURL();
            List<JSONObject> gameDetailList = steamPowered.getTopSellerGames();
            steamPowered.printDetails(gameDetailList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            steamPowered.quitDriver();
        }
    }

    // Step 1: Go to this url https://store.steampowered.com/
    private void gotoURL() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        this.webDriver = new ChromeDriver();
        this.webDriver.get("https://store.steampowered.com/");
        TimeUnit.SECONDS.sleep(6L);
    }

    // Step 2: Print the game names from the Top Seller section in console.
    private List<JSONObject> getTopSellerGames() throws InterruptedException {
        Actions actions = new Actions(this.webDriver);
        List<JSONObject> gameDetailList = new ArrayList<>();

        // Go to top sellers container
        WebElement topSellerContainer = this.webDriver.findElement(By.id("topsellers_tier"));
        topSellerContainer.click();
        List<Element> elementList = Jsoup.parse(topSellerContainer.getAttribute("innerHTML")).getElementsByTag("a");
        // Traverse thru each of the games and identify the corresponding hover container to identify the game title
        for (int i = 1; i <= elementList.size(); i++) {
            JSONObject gameDetails = new JSONObject();
            WebElement anchorTag = this.webDriver.findElement(By.xpath("//div[@id=\"topsellers_tier\"]/a[" + i + "]"));
            // Get title
            actions.moveToElement(anchorTag).build().perform();
            TimeUnit.SECONDS.sleep(1L);
            String appId = anchorTag.getAttribute("onmouseover");
            JSONObject jsonObject = new JSONObject(appId.substring(appId.indexOf("{"), appId.indexOf("}") + 1));
            gameDetails.put("name", this.webDriver.findElement(By.xpath("//div[@id=\"hover_app_" + jsonObject.get("id") + "\"]/h4")).getText());
            gameDetails.put("release_date", this.webDriver.findElement(By.xpath("//div[@id=\"hover_app_" + jsonObject.get("id") + "\"]/div[@class=\"hover_release\"]/span")).getText());

            try {
                // Step 3: From that section, categorize the games into three types - Free to play, on regular price, on sale
                Element discountPrices = Jsoup.parse(anchorTag.findElement(By.xpath("//div[@id=\"topsellers_tier\"]/a[" + i + "]/*/div[@class=\"discount_prices\"]")).getAttribute("innerHTML"));
                if (discountPrices.getElementsByAttributeValueContaining("class", "discount_original_price").hasText()) {
                    gameDetails.put("category", "On Sale");
                } else if (discountPrices.selectXpath("//div[@class=\"discount_final_price\"]").text().equalsIgnoreCase("Free to Play")) {
                    gameDetails.put("category", "Free to Play");
                } else {
                    gameDetails.put("category", "On Regular Price");
                }
                gameDetails.put("price", discountPrices.selectXpath("//div[@class=\"discount_final_price\"]").text());
            } catch (Exception e) {
                gameDetails.put("category", "block or empty");
                gameDetails.put("price", "null");
            } finally {
                gameDetailList.add(gameDetails);
            }
        }
        return gameDetailList;
    }

    // Step 4: Print the name of the games in ascending order in a csv file with four attributes
    // (name, release date, price and the above category)
    private void printDetails(List<JSONObject> gameDetailList) throws FileNotFoundException {
        gameDetailList.sort(((o1, o2) -> o1.getString("name").compareToIgnoreCase(o2.getString("name"))));
        gameDetailList.forEach(System.out::println);

        // write the data into csv;
        PrintWriter printWriter = new PrintWriter("bottom_movies.csv");
        // Header
        printWriter.write("name, release_date, price, category\n");
        gameDetailList.forEach(o1 -> {
            StringBuilder str = new StringBuilder();
            str.append(o1.getString("name")).append(",\"")
                    .append(o1.getString("release_date").split(":")[1].trim()).append("\",\"")
                    .append(o1.getString("price")).append("\",\"")
                    .append(o1.getString("category")).append(("\"\n"));
            printWriter.write(str.toString());
        });
        printWriter.close();
    }

    // close and quit
    private void quitDriver() {
        this.webDriver.close();
        this.webDriver.quit();
    }
}
