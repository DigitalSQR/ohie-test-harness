package com.tht.test;

import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.ArrayList;
import java.util.List;
import java.util.Set;
//import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tht.test.utiliy.ScreenShot;

public class Manual_Automation_Checks extends PageSetup {
	
	String component = "Test_Automaiton" + getCurrentTimeInMMSS();

	public static String getCurrentTimeInMMSS() {
		SimpleDateFormat sdf = new SimpleDateFormat("mmss");
		return sdf.format(new Date());
	}


	public static void main(String[] args) throws InterruptedException {
		Manual_Automation_Checks checks = new Manual_Automation_Checks();
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
		ScreenShot.createScreenshot("Login Successful");
		WebDriverWait wait = new WebDriverWait(driver, 60);
		WebDriverWait wait1 = new WebDriverWait(driver, 600);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Sidebar-applications']"))).click();
		ScreenShot.createScreenshot("navigate to Application from Dashboard");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='applications-acceptTestRequest-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='applications-accept-okButton']")))
				.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[contains(text(), 'Application testing request has been accepted successfully!')]")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(
				By.xpath("//*[contains(text(), 'Application testing request has been accepted successfully!')]")));

		wait.until(ExpectedConditions.invisibilityOfElementLocated(
				By.xpath("//*[contains(text(), 'Application testing request has been accepted successfully!')]")));
		ScreenShot.createScreenshot("Application testing request has been accepted successfully!");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='applications-actions-0']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='chooseTest-manualVerification']")))
				.click();
		ScreenShot.createScreenshot("Clicked on Manual Varification");

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.1.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-0"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-0']"))).click();

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.2.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-1']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-1"))).sendKeys("Test");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-1']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.3.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-2']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-2"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-2']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.4.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-3']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-3"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-3']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.5.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-4']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-4"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-4']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.6.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-5']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-5"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-5']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

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
		ScreenShot.createScreenshot("CFR 1 is completed");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")))
				.click();

		Thread.sleep(3000);
		System.out.println("Successfully clicked on save button.");

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.2.1.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-0"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-0']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.2.2.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-1']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-1"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-1']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.2.3.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-2']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-2"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-2']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

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
		ScreenShot.createScreenshot("CRF 2 is completed");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")))
				.click();

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.4.1.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-0"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-0']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

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
		ScreenShot.createScreenshot("CRF 4 is completed");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")))
				.click();

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.5.1.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-0"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-0']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

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
		ScreenShot.createScreenshot("CRF 5 is completed");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")))
				.click();

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.6.1.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-0"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-0']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.6.2.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-1']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-1"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-1']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.6.3.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-2']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-2"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-2']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.6.4.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-3']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-3"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-3']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

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
		ScreenShot.createScreenshot("CRF 6 is completed");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")))
				.click();

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.8.2.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-0"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-0']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.8.3.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-1']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-1"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-1']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.8.4.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-2']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-2"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-2']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

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
		ScreenShot.createScreenshot("CRF 8 is completed");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")))
				.click();

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.9.1.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-0"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-0']"))).click();
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.9.2.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-1']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-1"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-1']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));

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
		ScreenShot.createScreenshot("CRF 9 is completed");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")))
				.click();

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.10.5.option.1']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("VerticalOptions-1-0"))).sendKeys("Test");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='VerticalOptions-5-0']"))).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Notes saved successfully')]")));
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
		ScreenShot.createScreenshot("CRF 10 is completed");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")))
				.click();

		// Automation check
		ScreenShot.createScreenshot("Automation Test Started");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='unblockedNavToChooseTest']"))).click();
		wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='chooseTest-startAutomatedVerification']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Start Testing']"))).click();
		ScreenShot.createScreenshot("Automation Test Started");
		wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='chooseTest-startAutomatedVerification']")))
				.click();
		wait1.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("((//*[@id='automatedTesting-toggleComponent-0'])[1]//*[@class='d-flex align-items-center mb-0 test-result-row'])[1]")));
		wait1.until(ExpectedConditions.invisibilityOfElementLocated(
				By.xpath("((//*[@id='automatedTesting-toggleComponent-0'])[1]//*[@class='d-flex align-items-center mb-0 test-result-row'])[1]")));
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span/span")));

		String Result = driver.findElement(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span/span"))
				.getText();
		System.out.println("Current Result of your automated test is: " + Result + ".");

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span[2]")));
		String timeTaken = driver.findElement(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span[2]"))
				.getText();
		System.out.println("Time taken to execute your automated tests: " + timeTaken + ".");
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='automatedTesting-navToChooseTest']")))
				.click();
		String originalWindow = driver.getWindowHandle();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='chooseTest-Submit']"))).click();
		ScreenShot.createScreenshot("Automation Test Completed");
		wait.until(ExpectedConditions.numberOfWindowsToBe(2));
		Set<String> windowHandles = driver.getWindowHandles();
		for (String windowHandle : windowHandles) {
			if (!originalWindow.contentEquals(windowHandle)) {
				driver.switchTo().window(windowHandle);
				break;
			}
		}
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='report-generatePdf']"))).click();
		ScreenShot.createScreenshot("Report has been generated");
		driver.close();
		driver.switchTo().window(originalWindow);
		ScreenShot.createPdfFromScreenshots("Manual_AutomationTest" + component);
		driver.quit();
	}
}