package com.tht.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium	.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tht.test.utiliy.ScreenShot;

public class TestCase_Configuration extends PageSetup {

	String component = "Test_Automaiton" + getCurrentTimeInMMSS();

	public static String getCurrentTimeInMMSS() {
		SimpleDateFormat sdf = new SimpleDateFormat("mmss");
		return sdf.format(new Date());
	}

	String time = "SpecificationCreationTime" + getCurrentTimeInMMSS();

	public static String getCurrentTimeInMMSS1() {
		SimpleDateFormat sdf = new SimpleDateFormat("mmss");
		return sdf.format(new Date());
	}

	public static void main(String[] args) throws InterruptedException {
		TestCase_Configuration checks = new TestCase_Configuration();
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

		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#Sidebar-testcaseConfig']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentList-navToValidateConfig']")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='button']"))).click();
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Components']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentList-createComponent']")))
				.click();
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys(component);
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='description']")))
				.sendKeys("Testing_Automation_Script_To_Update_Description");
		Thread.sleep(2000);
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-cancel']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-submitButton']")))
				.click();
		try {
			scrollToBottom();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@aria-label='Go to page 4']"))).click();
		Thread.sleep(2000);
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//td[text()='" + component + "']/following-sibling::td/button")))
				.click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentList-deactivateComponent-cancelButton']"))).click();
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='componentList-deactivateComponent-okButton']"))).click();

		// Edit configuration
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[text()='" + component + "']/following-sibling::td/span[1]")))
				.click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys("Edited" + component);
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='description']")))
				.sendKeys("Edited Description");
		Thread.sleep(2000);

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-submitButton']")))
//				.click();
//		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='componentUpsertModal-cancel']"))).click();
		Thread.sleep(2000);

		// Edit specification
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[text()='" + component + "']/following-sibling::td/span[2]")))
				.click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='componentSpecification-createSpefication']"))).click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys(time);
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='description']"))).sendKeys("Description");
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-functional']")))
				.click();
		Thread.sleep(2000);

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

//		wait.until(ExpectedConditions
//				.elementToBeClickable(By.xpath("//*[@id='compSpecUpsertModal-submit']"))).click();
//		Thread.sleep(2000);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//td[text()='" + time + "']/following-sibling::td/button"))).click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@id='componentSpecification-Ok-inactiveButton']"))).click();
		Thread.sleep(2000);

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

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='specQuestions-createManualTestcase']")))
				.click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys(
				"1. Is there an interface that shows weandsdf pre-defined qwerwertexample of patient2 schema? The patient schema contains various attributes defining a patient's record. For instance a patient schema can have attributes such as first name, family name, gender, date of birth and address");
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@value='SINGLE_SELECT']"))).click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='#SpecQuestionsUpsertModal-submit']")))
				.click();
		Thread.sleep(5000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='editQuestion-addOption']"))).click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys("Yes");
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
				.sendKeys("True");
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
				.sendKeys(Keys.ENTER);
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='testcaseOptionUpsert-submit']"))).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='editQuestion-addOption']"))).click();
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='name']"))).sendKeys("NO_It's_False");
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
				.sendKeys("False");
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='react-select-2-input']")))
				.sendKeys(Keys.ENTER);
		Thread.sleep(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='testcaseOptionUpsert-submit']"))).click();
		Thread.sleep(2000);

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

//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='editQuestion-updateQuestion']")))
//		.click();
//		Thread.sleep(2000);

//		//*[@id='editQuestion-uploadImageForOption']
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='testcaseOptionUpsert-cancel']")))
//		.click();
//		Thread.sleep(2000);
		ScreenShot.createPdfFromScreenshots("Testcase_Configuration" + component);
		driver.quit();
	}
}
