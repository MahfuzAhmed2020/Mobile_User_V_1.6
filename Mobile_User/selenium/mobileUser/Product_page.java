package com.selenium.mobileUser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Product_page {

	WebDriver driver;

	public Product_page(WebDriver driver) {
		this.driver = driver;
	}

	By click_product_A = By.xpath("//*[@id=\"products\"]/div[1]/button");
	By click_product_B = By.xpath("//*[@id=\"products\"]/div[2]/button");
	By click_product_C = By.xpath("//*[@id=\"products\"]/div[3]/button");
	By click_product_D = By.xpath("//*[@id=\"products\"]/div[4]/button");
	By click_product_E = By.xpath("//*[@id=\"products\"]/div[5]/button");
	
	By Select_card_411 = By.xpath("//*[@id=\"card\"]/option[2]");
	By Select_card_422 = By.xpath("//*[@id=\\\"card\\\"]/option[3]");
	By Select_card_555 = By.xpath("//*[@id=\\\"card\\\"]/option[4]");
	By Select_card_378 = By.xpath("//*[@id=\\\"card\\\"]/option[5]");
	
	
	
	By Select_address_123_main = By.xpath("//*[@id=\"address\"]/option[2]");
	By Select_address_456_oak = By.xpath("//*[@id=\\\"address\\\"]/option[3]");
	By Select_address_789_pine = By.xpath("//*[@id=\\\"address\\\"]/option[4]");
	By Select_address_101_Maple = By.xpath("//*[@id=\\\"address\\\"]/option[5]");
	
	//checkout
	By checkout = By.xpath("//*[@id=\"Checkout\"]");
	
	
	// my order
	By my_order = By.xpath("//*[@id=\"My_Orders\"]");
	
	// Click on  card dropdowns
	By click_card_dropdown = By.xpath("//*[@id=\"card\"]");
	// Click on  address dropdowns
	By click_address_dropdown = By.xpath("//*[@id=\"address\"]");
	
	By successBox = By.id("successBox");
	By trackingNumber = By.id("trackingNumber");
	
	// click on product
	public void click_product_A() {
		driver.findElement(click_product_A).click();
	}	
	
	public void click_product_B() {
		driver.findElement(click_product_B).click();
	}
	public void click_product_C() {
		driver.findElement(click_product_C).click();
	}
	public void click_product_D() {
		driver.findElement(click_product_D).click();
	}
	public void click_product_E() {
		driver.findElement(click_product_E).click();
	}

	// address
	public void click_card_dropdown() {
		driver.findElement(click_card_dropdown).click();
	}	
	// cards
	public void click_address_dropdown() {
		driver.findElement(click_address_dropdown).click();
	}
	
	// cards
	public void Select_card_411() {
		driver.findElement(Select_card_411).click();
	}
	
	public void Select_card_422() {
		driver.findElement(Select_card_422).click();
	}
	public void Select_card_555() {
		driver.findElement(Select_card_555).click();
	}
	public void Select_card_378() {
		driver.findElement(Select_card_378).click();
	}
	//address
	public void Select_address_123_main() {
		driver.findElement(Select_address_123_main).click();
	}
	public void Select_address_456_oak() {
		driver.findElement(Select_address_456_oak).click();
	}	
	public void Select_address_789_pine() {
		driver.findElement(Select_address_789_pine).click();
	}	
	public void Select_address_101_Maple() {
		driver.findElement(Select_address_101_Maple).click();
	}
	
	//checkout
	public void checkout() {
		driver.findElement(checkout).click();
	}
	
	//my_order
	public void my_order() {
		driver.findElement(my_order).click();
	}
	
	public boolean isSuccessVisible() {
		return driver.findElement(successBox).isDisplayed();
	}

	public String getTrackingNumber() {
		return driver.findElement(trackingNumber).getText();
	}
}
