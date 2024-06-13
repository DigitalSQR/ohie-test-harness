package com.tht.test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class login extends PageSetup {

	public login() {
	}

	public login(WebDriver driver) {
		this.driver = driver;
	}

	// Method to perform login
	public void performLogin(String username, String password) {
		WebDriver driver = getDriver();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		driver.get("https://tht-dev.argusservices.in/login");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("exampleFormControlInput1"))).sendKeys(username);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("exampleFormControlInput2"))).sendKeys(password);

		wait.until(ExpectedConditions.elementToBeClickable(By.id("exampleFormControlInput2"))).sendKeys(Keys.ENTER);

		// Optionally, perform additional actions after login
//       
	}

	public static void main(String[] args) {
		String username = "noreplytestharnesstool@gmail.com";
		String password = "password";
		if (args.length == 2) {
			username = args[0];
			password = args[1];
		}

		// Create an instance of the PageSetup class
		PageSetup pageSetup = new PageSetup();

		// Set the browser name
		pageSetup.setup("Chrome");

		// Create an instance of the login class and perform login
		login loginPage = new login();
		loginPage.performLogin(username, password);

		// Quit the driver
		pageSetup.quitDriver();
	}
}
