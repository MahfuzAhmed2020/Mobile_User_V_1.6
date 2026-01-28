package com.selenium.mobileUser;



import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutFlowTest extends BaseTest {

    @Test
    public void completeCheckoutFlow() throws InterruptedException {

        TestLogger.step("Open login page");
        driver.get("http://localhost/Mobile_User/public/login.php");
        Thread.sleep(2000);
        LoginPage login = new LoginPage(driver);
        Thread.sleep(2000);
        TestLogger.step("Login with valid credentials");
        Thread.sleep(2000);
        login.login("rock@gmail.com", "Test123!");
        Thread.sleep(2000);

        
        // Add product
        Product_page pp=new Product_page(driver);
        TestLogger.step("Open profile to add product");
        Thread.sleep(2000);
        driver.get("http://localhost/Mobile_User/public/profile.php");
        pp.click_product_A();
        
        Thread.sleep(5000);
        driver.navigate().refresh();
        Thread.sleep(5000);
        pp.click_card_dropdown();
        Thread.sleep(5000);
        pp.Select_card_411();
        Thread.sleep(5000);
        pp.click_address_dropdown();
        Thread.sleep(5000);
        pp.Select_address_123_main();
        Thread.sleep(5000);
        pp.checkout();

        
        Thread.sleep(2000);
     // Wait for alert to be present
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());

        // Switch to and accept the alert
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText(); // Get full alert text
        alert.accept(); // Close the alert

        // Extract tracking number
        String trackingNumber = alertText.replace("Checkout successful\nTracking Number: ", "");
        System.out.println("Tracking Number: " + trackingNumber); // D1EA0215A1 
        
        //Thread.sleep(5000);
        

     
        
//        Thread.sleep(2000);
//        TestLogger.step("Open checkout page");
//        driver.get("http://localhost/Mobile_User/public/checkout.php");
//        
//        Thread.sleep(2000);
//
//        CheckoutPage checkout = new CheckoutPage(driver);
//
//        TestLogger.step("Place order");
//        checkout.placeOrder();
//        Thread.sleep(3000);
//
//        TestLogger.verify("Order success message should be visible");
//        Assert.assertTrue(checkout.isSuccessVisible());
//
//        String tracking = checkout.getTrackingNumber();
//        TestLogger.verify("Tracking number generated: " + tracking);
//        Assert.assertNotNull(tracking);
//
//        TestLogger.step("Open orders page");
//        driver.get("http://localhost/Mobile_User/public/orders.php");
//        Thread.sleep(2000);
//
//        OrdersPage orders = new OrdersPage(driver);
//
//        TestLogger.verify("Order should appear in order list");
//        Assert.assertTrue(orders.isTrackingVisible(tracking));
    }
}
