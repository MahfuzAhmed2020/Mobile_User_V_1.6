package com.selenium.mobileUser;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage {

    WebDriver driver;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    By placeOrderBtn = By.xpath("//button[contains(text(),'Place Order')]");
    By successBox = By.id("successBox");
    By trackingNumber = By.id("trackingNumber");

    public void placeOrder() {
        driver.findElement(placeOrderBtn).click();
    }

    public boolean isSuccessVisible() {
        return driver.findElement(successBox).isDisplayed();
    }

    public String getTrackingNumber() {
        return driver.findElement(trackingNumber).getText();
    }
}
