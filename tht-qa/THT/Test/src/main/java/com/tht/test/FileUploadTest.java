package com.tht.test;

import org.openqa.selenium.WebDriver;

public class FileUploadTest {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide browser type as a command line argument.");
            return;
        }

        String browserType = args[0];

        // Create an instance of the PageSetup class
        PageSetup pageSetup = new PageSetup();
        pageSetup.setup(browserType);
        

        // Get the WebDriver instance
        @SuppressWarnings("unused")
		WebDriver driver = pageSetup.getDriver();

        // Perform file upload
    //    pageSetup.uploadFile();

        // Quit the driver
        pageSetup.quitDriver();
    }
}
