package com.selenium.mobileUser;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrdersPage {

    WebDriver driver;

    public OrdersPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isTrackingVisible(String tracking) {
        return driver.getPageSource().contains(tracking);
    }
}
