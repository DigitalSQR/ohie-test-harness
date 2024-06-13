package com.tht.test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.tht.test.utiliy.ScreenShot;

public class Sanity_Test_For_Whole_Site extends PageSetup {

	String component = "Test_Automaiton" + getCurrentTimeInHHMMSS();

	public static String getCurrentTimeInHHMMSS() {
		SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
		return sdf.format(new Date());
	}

	String time = "SpecificationCreationTime" + getCurrentTimeInHHMMSS();

	public static String getCurrentTimeInHHMMSS1() {
		SimpleDateFormat sdf = new SimpleDateFormat("mmss");
		return sdf.format(new Date());
	}

	public static void main(String[] args) throws InterruptedException {
		Sanity_Test_For_Whole_Site checks = new Sanity_Test_For_Whole_Site();
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
		WebDriverWait wait = new WebDriverWait(driver, 200);
		WebDriverWait wait1 = new WebDriverWait(driver, 600);

		driver.get("https://tht-dev.argusservices.in/");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='exampleFormControlInput1']")).sendKeys("jedawe8997@kernuo.com");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='exampleFormControlInput2']")).sendKeys("password");
		Thread.sleep(2000);
		driver.findElement(By.id("#Login-login")).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Login Successfully");
		driver.findElement(By.xpath("//button[@id='#Dashboard-registerTestRequest']")).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Navigated to Test Request Page Successfully");

		driver.findElement(By.xpath("//input[@id='name']")).sendKeys("Test");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//label[text()='Client Registry (CR)']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='testRequestUrls[0].username']")).sendKeys("12345");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='testRequestUrls[0].password']")).sendKeys("1234567");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//*[@id='testRequestUrls[0].websiteUIBaseUrl']"))
				.sendKeys("https://hapi.fhir.org/baseR4");
		driver.findElement(By.xpath("//*[@id='testRequestUrls[0].fhirApiBaseUrl']"))
				.sendKeys("https://hapi.fhir.org/baseR4");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(25000,0)", "");
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
		Thread.sleep(2000);
		driver.findElement(By.xpath("//button[text()='Submit']")).click();
		ScreenShot.createScreenshot("Test Request has been created");

		// Update Profile
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='header-userProfile']"))).click();
		ScreenShot.createScreenshot("Profile opend to update");

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
		ScreenShot.createScreenshot("Profile picture added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'OK')]"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Profile picture updated");

//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Header-logout']")));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='UserProfile-name']")))
				.sendKeys("Test_Profile_Automation");
		ScreenShot.createScreenshot("Name added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='UserProfile-companyName']")))
				.sendKeys("Argus");
		ScreenShot.createScreenshot("Company name has been added");

		try {
			scrollToBottom();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='#UserProfile-handleCancel']")))
				.click();
		ScreenShot.createScreenshot("Cancel button is clicked");

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//button[@id='UserProfile-discardChanges-okButton']"))).click();
		ScreenShot.createScreenshot("Cancel Confirmation ok is clikced");

//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='roleIds']"))).sendKeys("Admin");
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='email']"))).sendKeys("TestMail@gmail.com");
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#UserProfile-update']"))).click();

//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#UserProfile-resetPassword']"))).click();
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ResetPassword-oldPassword']"))).sendKeys("Test@123");
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ResetPassword-newPassword']"))).sendKeys("Test@1234");
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ResetPassword-confirmPassword']"))).sendKeys("Test@1234");
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#ResetPassword-Reset']"))).click();
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#ResetPassword-cancel']"))).click();

//				Notification Archive 
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-icon='bell']"))).click();
//				 try {
//			            scrollToBottom();
//			        } catch (InterruptedException e) {
//			            e.printStackTrace();
//			        }
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Header-bulkArchiveNotifications']"))).click();
//				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-icon='bell']"))).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='header-userProfile']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Header-logout']"))).click();

		ScreenShot.createScreenshot("Logout Successfully");

		loginPage.performLogin("noreplytestharnesstool@gmail.com", "password");
		ScreenShot.createScreenshot("Login Successfully");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Sidebar-applications']"))).click();
		ScreenShot.createScreenshot("Navigate to Application from Dashboard");
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
			List<WebElement> radioButtons = driver.findElements(By.xpath("//label[contains(text(),'Yes')]"));
			for (int i = 0; i < radioButtons.size(); i++) {
				radioButtons.get(i).click();
				Thread.sleep(2000);
			}
			List<WebElement> attachmentButtons = driver.findElements(By.xpath("//input[@type='file']")); // Adjust the
			// Find all attachment buttons // XPath as
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
		ScreenShot.createScreenshot("Automation selected");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='unblockedNavToChooseTest']"))).click();
		wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='chooseTest-startAutomatedVerification']")))
				.click();
//		if (wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Start Testing']")))
//				.isDisplayed()) {
//			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Start Testing']"))).click();
//		}
//		ScreenShot.createScreenshot("Automation Test Start");
//		wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
//				"((//*[@id='automatedTesting-toggleComponent-0'])[1]//*[@class='spinner-border spinner-border-sm'])")));
//		wait1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
//				"((//*[@id='automatedTesting-toggleComponent-0'])[1]//*[@class='spinner-border spinner-border-sm'])")));
//		wait.until(ExpectedConditions
//				.visibilityOfElementLocated(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span/span")));
//		String Result = driver.findElement(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span/span"))
//				.getText();
//		ScreenShot.createScreenshot("Automation Test Started");
//		System.out.println("Current Result of your automated test is: " + Result + ".");
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Start Testing']")));
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Start Testing']"))).click();
			
			ScreenShot.createScreenshot("Automation Test Start");
			wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
					"((//*[@id='automatedTesting-toggleComponent-0'])[1]//*[@class='spinner-border spinner-border-sm'])")));
			wait1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
					"((//*[@id='automatedTesting-toggleComponent-0'])[1]//*[@class='spinner-border spinner-border-sm'])")));
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span/span")));
			String Result = driver.findElement(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span/span"))
					.getText();
			ScreenShot.createScreenshot("Automation Test Started");
			System.out.println("Current Result of your automated test is: " + Result + ".");

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span[2]")));
			String timeTaken = driver.findElement(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span[2]"))
					.getText();
			System.out.println("Time taken to execute your automated tests: " + timeTaken + ".");
			Thread.sleep(2000);
		} catch (Exception e) {
			ScreenShot.createScreenshot("Automation Test Start");
			wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
					"((//*[@id='automatedTesting-toggleComponent-0'])[1]//*[@class='spinner-border spinner-border-sm'])")));
			wait1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
					"((//*[@id='automatedTesting-toggleComponent-0'])[1]//*[@class='spinner-border spinner-border-sm'])")));
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span/span")));
			String Result = driver.findElement(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span/span"))
					.getText();
			ScreenShot.createScreenshot("Automation Test Started");
			System.out.println("Current Result of your automated test is: " + Result + ".");

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span[2]")));
			String timeTaken = driver.findElement(By.xpath("//*[@id='automatedTesting-toggleComponent-0']//span[2]"))
					.getText();
			System.out.println("Time taken to execute your automated tests: " + timeTaken + ".");
			Thread.sleep(2000);
		}

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

		// Test Case Configuration

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Sidebar-testcaseConfig']"))).click();
		ScreenShot.createScreenshot("TestCase Configuration is clicked successfully");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentList-navToValidateConfig']")))
				.click();
		ScreenShot.createScreenshot("Validate Configuration is clicked and opened successfully");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='button']"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("What Does This Data Represent is expanded successfully");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Components']"))).click();
		ScreenShot.createScreenshot("Back to Component Configuration screen successfully");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentList-createComponent']")))
				.click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Create Component clicked successfully");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys(component);
		Thread.sleep(2000);
		ScreenShot.createScreenshot("name added successfully");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='description']")))
				.sendKeys("Testing_Automation_Script_To_Update_Description");
		Thread.sleep(2000);
		ScreenShot.createScreenshot("description added successfully");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-cancel']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-submitButton']")))
				.click();
		try {
			scrollToBottom();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ScreenShot.createScreenshot("Submit button has been clicked successfully");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='ComponentPage-last-page-button']")))
				.click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Last page has been open to click find recently created component");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//td[text()='" + component + "']/following-sibling::td/button")))
				.click();
		ScreenShot.createScreenshot("Component has been clicked");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentList-deactivateComponent-cancelButton']"))).click();
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='componentList-deactivateComponent-okButton']"))).click();
		ScreenShot.createScreenshot("Component has been disabled");

		// Edit configuration

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[text()='" + component + "']/following-sibling::td/span[1]")))
				.click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component Edit has clicked");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys("Edited" + component);
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component Name has been edited");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='description']")))
				.sendKeys("Edited Description");
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component description has been edited");

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-submitButton']")))
//				.click();
//		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-cancel']"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Cancel button has been clicked");

		// Edit specification
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[text()='" + component + "']/following-sibling::td/span[2]")))
				.click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component Edit button has been clicked");

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='componentSpecification-createSpefication']"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component specification create button has been clicked");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys(time);
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Name has been entered to create component");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='description']"))).sendKeys("Description");
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component decription has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-functional']")))
				.click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component type has been clicked");

//		wait.until(ExpectedConditions
//				.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-workflow']"))).click();
//		Thread.sleep(2000);
//		
//		wait.until(ExpectedConditions
//				.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-required']"))).click();
//		Thread.sleep(2000);
//		
//		wait.until(ExpectedConditions
//				.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-recommended']"))).click();
//		Thread.sleep(2000);

//		wait.until(ExpectedConditions
//				.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-cancel']"))).click();
//		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-submit']"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Submit button has been clicked");

//		wait.until(ExpectedConditions
//				.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-submit']"))).click();
//		Thread.sleep(2000);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//td[text()='" + time + "']/following-sibling::td/button"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component toggle button has been clicked");

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='componentSpecification-Ok-inactiveButton']"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Toggle has been deactivated");

//		wait.until(ExpectedConditions
//				.elementToBeClickable(By.xpath("//*[text()='" + component + "']/following-sibling::td/span[1]")))
//				.click();
//		Thread.sleep(2000);

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys(time);
//		Thread.sleep(2000);
//
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='description']"))).sendKeys("Description");
//		Thread.sleep(2000);

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-submitButton']")))
//				.click();
//		Thread.sleep(2000);

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-cancel']")))
//		.click();
//		Thread.sleep(2000);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[text()='" + time + "']/following-sibling::td//span[2]/i"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Component specification button has been clicked");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='specQuestions-createManualTestcase']")))
				.click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Create new testcase button has been clicked");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys(
				"1. Does the system ffindssicatwesss ssfsmulti-bdfirths in ass family? Whesn there are twins, triplets, or more children born at the same time, the system should be able to indicate that.\n"
						+ "");
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Question has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@value='SINGLE_SELECT']"))).click();
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Answer type has been selected");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#SpecQuestionsUpsertModal-submit']")))
				.click();

		ScreenShot.createScreenshot("Submit has been clicked");
		Thread.sleep(6000);
		
		try {
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='editQuestion-addOption']"))).click();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Question edit button has been clicked");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']")))
				.sendKeys("No,It's wrong answer");
		Thread.sleep(2000);
		ScreenShot.createScreenshot("Name has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='combobox']"))).sendKeys("False");
		Thread.sleep(2000);
		ScreenShot.createScreenshot("False option from dropdown has been clicked");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='combobox']"))).sendKeys(Keys.ENTER);
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='testcaseOptionUpsert-submit']"))).click();
		ScreenShot.createScreenshot("Submit button has been clicked");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='editQuestion-addOption']"))).click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']")))
				.sendKeys("YES, This is true answer");
		Thread.sleep(2000);

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']"))).click();
//		Thread.sleep(2000);

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
//				.sendKeys("False");
//		Thread.sleep(2000);
//		ScreenShot.createScreenshot("False option from dropdown has been clicked");
//
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
//				.sendKeys(Keys.ENTER);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='testcaseOptionUpsert-submit']"))).click();
		ScreenShot.createScreenshot("Submit button has been clicked");
		Thread.sleep(2000);
		// User management

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
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='combobox']"))).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='combobox']"))).sendKeys("Tester");
		ScreenShot.createScreenshot("Tester Role has been selected");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='combobox']"))).sendKeys(Keys.ENTER);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='combobox']"))).sendKeys("Admin");
		ScreenShot.createScreenshot("Admin Role has been added");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='combobox']"))).sendKeys(Keys.ENTER);

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

//		wait.until(ExpectedConditions.elementToBeClickable(
//				By.xpath("//tr[td[text()='" + randomEmail + "']]//button[contains(@id,'adminUsers-switch-status')]")))
//				.click();
//		ScreenShot.createScreenshot("Created user has been clicked to inactive user");

		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
					"//tr[td[text()='" + randomEmail + "']]//button[contains(@id,'adminUsers-switch-status')]")));
			wait.until(ExpectedConditions.elementToBeClickable(By
					.xpath("//tr[td[text()='" + randomEmail + "']]//button[contains(@id,'adminUsers-switch-status')]")))
					.click();
			ScreenShot.createScreenshot("Created user has been clicked to inactive user");
		} catch (Exception e) {
			try {
				scrollToBottom();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
						"//tr[td[text()='" + randomEmail + "']]//button[contains(@id,'adminUsers-switch-status')]")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
						"//tr[td[text()='" + randomEmail + "']]//button[contains(@id,'adminUsers-switch-status')]")))
						.click();
				ScreenShot.createScreenshot("Created user has been clicked to inactive user");
			} catch (Exception e2) {
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Go to next page']")))
						.click();
				try {
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[td[text()='" + randomEmail
							+ "']]//button[contains(@id,'adminUsers-switch-status')]")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[td[text()='" + randomEmail
							+ "']]//button[contains(@id,'adminUsers-switch-status')]"))).click();
					ScreenShot.createScreenshot("Created user has been clicked to inactive user");
				} catch (Exception e3) {
					scrollToBottom();
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[td[text()='" + randomEmail
							+ "']]//button[contains(@id,'adminUsers-switch-status')]")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[td[text()='" + randomEmail
							+ "']]//button[contains(@id,'adminUsers-switch-status')]"))).click();
					ScreenShot.createScreenshot("Created user has been clicked to inactive user");
				}
			}
		}

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='adminUsers-ok-inactiveButton']")))
				.click();
		Thread.sleep(3000);
		ScreenShot.createScreenshot("User inactivated successfully");
		Thread.sleep(2000);
		ScreenShot.createPdfFromScreenshots("Sanity" + component);

		driver.quit();
	}

	String randomEmail = getRandomTestEmail();

	public String getRandomTestEmail() {
		List<String> testEmails = Arrays.asList("testemail12322@example.com", "testemail132323@example.com",
				"testemail5624@example.com", "testemail4525@example.com", "testemail221316@example.com",
				"testemail32427@example.com", "testemail285646@example.com", "testemail45629@example.com",
				"testemail4353430@example.com", "testemail345331@example.com", "testemail54611@example.com",
				"testemail4354312@example.com", "testemail3454313@example.com", "testemail546532@example.com",
				"testemail546533@example.com", "testemail4565434@example.com", "testemail5465435@example.com",
				"testemail546536@example.com"

		);
		Random random = new Random();
		int index = random.nextInt(testEmails.size());
		return testEmails.get(index);

	}
}