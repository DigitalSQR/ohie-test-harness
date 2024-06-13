package com.tht.test;

import java.text.SimpleDateFormat;
//import java.sql.Driver;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tht.test.utiliy.ScreenShot;

public class User_management extends PageSetup {
	
	String component = "Test_Automaiton" + getCurrentTimeInMMSS();

	public static String getCurrentTimeInMMSS() {
		SimpleDateFormat sdf = new SimpleDateFormat("mmss");
		return sdf.format(new Date());
	}


	public static void main(String[] args) throws InterruptedException {
		User_management checks = new User_management();
		checks.runAutomation();
	}

	public void runAutomation() throws InterruptedException {
		setup("Chrome");
		WebDriver driver = getDriver();
		if (driver == null) {
			System.out.println("Driver initialization failed.");
			return;
		}
		String randomEmail = getRandomTestEmail();
		login loginPage = new login(getDriver());
		loginPage.performLogin("noreplytestharnesstool@gmail.com", "password");
		ScreenShot.createScreenshot("Admin Logged IN");

		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Sidebar-UserManagement']"))).click();
		ScreenShot.createScreenshot("User management has been clicked");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='adminUser-createUser']"))).click();
		ScreenShot.createScreenshot("Create new users button has been clicked");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys("Akash Chavda");
		ScreenShot.createScreenshot("Name has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='email']"))).sendKeys(randomEmail);
		ScreenShot.createScreenshot("Email has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='password']"))).sendKeys("password");
		ScreenShot.createScreenshot("Password has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='confirmPassword']")))
				.sendKeys("password");
		ScreenShot.createScreenshot("Comfirm Password has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
				.sendKeys("Tester");
		ScreenShot.createScreenshot("Tester Role has been selected");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
				.sendKeys(Keys.ENTER);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
				.sendKeys("Admin");
		ScreenShot.createScreenshot("Admin Role has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
				.sendKeys(Keys.ENTER);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]"))).click();
		ScreenShot.createScreenshot("Submit button has been clicked");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='addAdminUser-resetForm']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='header-userProfile']"))).click();
		ScreenShot.createScreenshot("User profile has been clicked to logout");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Header-logout']"))).click();
		ScreenShot.createScreenshot("Logout successfully");
		loginPage.performLogin(randomEmail, "password");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='header-userProfile']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Header-logout']"))).click();
		loginPage.performLogin("noreplytestharnesstool@gmail.com", "password");
		ScreenShot.createScreenshot("Admin has been logged in");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Sidebar-UserManagement']"))).click();
		ScreenShot.createScreenshot("User management has been clicked from menu");

		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//tr[td[text()='" + randomEmail + "']]//button[contains(@id,'adminUsers-switch-status')]")))
				.click();
		ScreenShot.createScreenshot("Created user has been clicked to inactive user");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='adminUsers-ok-inactiveButton']")))
				.click();
		Thread.sleep(3000);
		ScreenShot.createScreenshot("User inactivated successfully");

		ScreenShot.createPdfFromScreenshots("User management" + component);

		driver.quit();
	}

	public String getRandomTestEmail() {
		List<String> testEmails = Arrays.asList("testemail1@example.com", "testemail2@example.com",
				"testemail3@example.com", "testemail4@example.com", "testemail5@example.com", "testemail6@example.com",
				"testemail7@example.com", "testemail8@example.com", "testemail9@example.com", "testemail10@example.com",
				"testemail11@example.com", "testemail12@example.com", "testemail13@example.com",
				"testemail14@example.com", "testemail15@example.com", "testemail16@example.com",
				"testemail17@example.com", "testemail18@example.com"

		);
		Random random = new Random();
		int index = random.nextInt(testEmails.size());
		return testEmails.get(index);
	}
}