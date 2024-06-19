package com.tht.test;

import java.util.List;

import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Update_Profile extends PageSetup {

	public static void main(String[] args) throws InterruptedException {
		Update_Profile checks = new Update_Profile();
		checks.runAutomation();
	}

	public void runAutomation() throws InterruptedException {
		setup("Chrome");
		WebDriver driver = getDriver();
		if (driver == null) {
			System.out.println("Driver initialization failed.");
			return;
		}

		login loginPage = new login(getDriver());
		loginPage.performLogin("noreplytestharnesstool@gmail.com", "password");

		WebDriverWait wait = new WebDriverWait(driver, 5);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='header-userProfile']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Header-userProfile']"))).click();

		try {
			// Find all attachment buttons
			List<WebElement> attachmentButtons = driver.findElements(By.xpath("//input[@type='file']")); // Adjust the
																											// XPath as
																											// necessary
			for (WebElement attachmentButton : attachmentButtons) {
				uploadImage(attachmentButton, "/src/NewPackage/Images/file.png");
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'OK')]"))).click();
		Thread.sleep(2000);
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Header-logout']")));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='UserProfile-name']")))
				.sendKeys("Test_Profile_Automation");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='UserProfile-companyName']")))
				.sendKeys("Argus");
		try {
			scrollToBottom();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='#UserProfile-handleCancel']")))
				.click();
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//button[@id='UserProfile-discardChanges-okButton']"))).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='roleIds']"))).sendKeys("Admin");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='email']"))).sendKeys("TestMail@gmail.com");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#UserProfile-update']"))).click();

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#UserProfile-resetPassword']"))).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ResetPassword-oldPassword']"))).sendKeys("Test@123");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ResetPassword-newPassword']"))).sendKeys("Test@1234");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ResetPassword-confirmPassword']"))).sendKeys("Test@1234");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#ResetPassword-Reset']"))).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#ResetPassword-cancel']"))).click();

//		Notification Archive 
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-icon='bell']"))).click();
//		 try {
//	            scrollToBottom();
//	        } catch (InterruptedException e) {
//	            e.printStackTrace();
//	        }
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Header-bulkArchiveNotifications']"))).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-icon='bell']"))).click();

		driver.quit();
	}
}
