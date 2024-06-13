package com.tht.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;

public class Assessee_login_to_test_request extends PageSetup {

	public static void main(String[] args) throws InterruptedException {
		Assessee_login_to_test_request checks = new Assessee_login_to_test_request();
		checks.runAutomation();
	}

	public void runAutomation() throws InterruptedException {
		setup("Chrome");
		WebDriver driver = getDriver();
		if (driver == null) {
			System.out.println("Driver initialization failed.");
			return;
		}

		driver.get("https://tht-dev.argusservices.in/");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='exampleFormControlInput1']")).sendKeys("automationqas30@gmail.com");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='exampleFormControlInput2']")).sendKeys("Automation@1234");
		Thread.sleep(2000);
		driver.findElement(By.id("#Login-login")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//button[@id='#Dashboard-registerTestRequest']")).click();
		Thread.sleep(2000);
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
		driver.quit();
	}
}