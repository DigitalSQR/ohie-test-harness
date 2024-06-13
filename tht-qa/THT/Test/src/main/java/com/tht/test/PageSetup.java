package com.tht.test;

import java.io.File;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class PageSetup {
	protected WebDriver driver; // Changed to non-static

	public void setup(String browser) {
		if (browser.equalsIgnoreCase("Chrome")) {
			System.setProperty("webdriver.chrome.driver", "/home/akash/Downloads/chromedriver-linux64/chromedriver");
			driver = new ChromeDriver();
		} else if (browser.equalsIgnoreCase("Firefox")) {
			System.setProperty("webdriver.gecko.driver", "/home/akash/Downloads/usr/bin/geckodriver");
			driver = new FirefoxDriver();
		} else if (browser.equalsIgnoreCase("Edge")) {
			System.setProperty("webdriver.edge.driver", "/home/akash/Downloads/usr/bin/msedgedriver");
			driver = new EdgeDriver();
		} else {
			System.out.println("Please enter any of the given browser names only. (i.e. 'Chrome', 'Firefox', 'Edge'.)");
		}

		if (driver != null) {
			driver.manage().window().maximize();
		}
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void quitDriver() {
		if (driver != null) {
			driver.quit();
		}
	}

	public void scrollToBottom() throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
		Thread.sleep(2000);
	}

	public void scrollBy(int x, int y) throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(arguments[0], arguments[1])", x, y);
		Thread.sleep(2000);
	}

	public void uploadImage(WebElement fileInputElement, String relativeFilePath) {
		String currentWorkingDir = System.getProperty("user.dir");
		String absoluteFilePath = currentWorkingDir + File.separator + relativeFilePath;
		File file = new File(absoluteFilePath);
		if (file.exists()) {
			fileInputElement.sendKeys(file.getAbsolutePath());
		} else {
			System.out.println("File not found: " + absoluteFilePath);
		}
	}

}
