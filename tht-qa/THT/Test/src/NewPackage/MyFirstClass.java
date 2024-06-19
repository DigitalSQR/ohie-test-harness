import java.io.File;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MyFirstClass {
	
	static WebDriver driver;	

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/home/akash/Downloads/chromedriver-linux64/chromedriver");
        
        
        
//        chromeOptions.addArguments("--start-maximized");
        
        
//         driver = new ChromeDriver(chromeOptions);
        
        driver.navigate().to("https://tht-dev.argusservices.in/");
        
//        WebDriverWait wait = new WebDriverWait(driver, 20);
        
        driver.findElement(By.id("exampleFormControlInput1")).sendKeys("noreplytestharnesstool@gmail.com");
        sleep(2000);
        
        driver.findElement(By.id("exampleFormControlInput2")).sendKeys("password");
        sleep(2000);
        
        driver.findElement(By.id("exampleFormControlInput2")).sendKeys(Keys.ENTER);
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#Sidebar-applications']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='applications-acceptTestRequest-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='applications-accept-okButton']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='applications-actions-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='applications-actions-4']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='chooseTest-resumeManualVerification']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.1.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        uploadFile(driver, "my-file-f3b04a28-29aa-4ecd-a404-44114e635470", "/src/NewPackage/Images/file.png");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.2.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-1']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-1")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-1']")).click();
        sleep(2000);
        
        uploadFile(driver, "my-file-e7005103-fb4b-4f39-a872-d3e67453da8d", "/src/NewPackage/Images/file.png");
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.3.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-2']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-2")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-2']")).click();
        sleep(2000);
        
        uploadFile(driver, "my-file-2f1139c1-6364-4ea7-be4e-355b2b1089b3", "/src/NewPackage/Images/file.png");
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.4.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-3']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-3")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-3']")).click();
        sleep(2000);
        
        uploadFile(driver, "my-file-8180330f-09a7-4239-af69-59556859a2a9", "/src/NewPackage/Images/file.png");
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.5.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-4']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-4")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-4']")).click();
        sleep(2000);
        
        uploadFile(driver, "my-file-78afc52f-9eaa-4eab-b745-59f47b8b8044", "/src/NewPackage/Images/file.png");
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.1.6.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-5']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-5")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-5']")).click();
        sleep(2000);
        
        uploadFile(driver, "my-file-295aedb9-90cb-4b81-87cc-119d3de56e93", "/src/NewPackage/Images/file.png");
        
        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.2.1.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.2.2.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.2.3.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
        sleep(2000);
        
//        //Login with google
//        driver.findElement(By.xpath("//*[@id=\"LoginWithGoogle\"]")).click();
//        
//        driver.findElement(By.xpath("//*[@id='identifierId']")).sendKeys("chavdaakash69@gmail.com");
//        
//        driver.findElement(By.xpath("//*[@id='identifierNext']")).click();
//       
//        driver.findElement(By.xpath("//*[@id='password']//input")).sendKeys("Akash972271");
//        
//        driver.findElement(By.xpath("//*[@id='passwordNext']")).click();
//        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.4.1.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.5.1.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.6.1.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.6.2.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.6.3.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.6.4.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.8.2.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.8.3.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.8.4.option.1']")).click();
        sleep(2000);
      
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.9.1.option.1']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.9.2.option.1']")).click();
        sleep(2000);
      
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.10.5.option.1']")).click();
        sleep(2000);
      
        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
        sleep(2000);
        
        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
        sleep(2000);
        
        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
        sleep(2000); }
        
//        
//        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-0']")).click();
//        sleep(2000);
//        
//        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
//        sleep(2000);
//        
//        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
//        sleep(2000);
       
        
        private static void uploadFile(WebDriver driver, String fileInputId, String relativeFilePath) {
            String currentWorkingDir = System.getProperty("user.dir");
            String absoluteFilePath = currentWorkingDir + relativeFilePath;

            File file = new File(absoluteFilePath);
            if (file.exists()) {
                driver.findElement(By.id(fileInputId)).sendKeys(file.getAbsolutePath());
            } else {
                System.out.println("File not found: " + absoluteFilePath);
            }
        
        
        
//        By fileInputLocator = By.id("my-file-fca390dc-8299-4a42-8d3d-afc80a61d096");
//        // Get the current working directory
//        String currentWorkingDir = System.getProperty("user.dir");
//
//        // Construct the absolute file path
//        String relativeFilePath = "/src/NewPackage/Images/file.png";
//        String absoluteFilePath = currentWorkingDir + relativeFilePath;
////        driver.findElement(fileInputLocator).sendKeys(absoluteFilePath);
//        
//        File file = new File(absoluteFilePath);
//        if (file.exists()) {
//            // Provide the absolute file path to the file input element
//            driver.findElement(fileInputLocator).sendKeys(file.getAbsolutePath());
//
//            // Optionally, submit the form or click the upload button if needed
//            // driver.findElement(By.id("upload-button")).click();
//
//            // Perform other actions or assertions here
//        } else {
//            System.out.println("File not found: " + absoluteFilePath);
//        }
        
//        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.8.3.option.1']")).click();
//        sleep(2000);
        
//        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-1']")).click();
//        sleep(2000);
//        
//        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
//        sleep(2000);
//        
//        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
//        sleep(2000);
        
//        By fileInputLocatortwo = By.id("my-file-b74b0073-65e3-4aa3-ace6-da638bb59da6");
//        // Get the current working directory
//        String currentWorkingDirtwo = System.getProperty("user.dir");
//
//        // Construct the absolute file path
//        String relativeFilePathtwo = "/src/NewPackage/Images/file.png";
//        String absoluteFilePathtwo = currentWorkingDirtwo + relativeFilePathtwo;
//	      driver.findElement(fileInputLocator).sendKeys(absoluteFilePath);
//        
//        File filee = new File(absoluteFilePathtwo);
//        if (filee.exists()) {
//            // Provide the absolute file path to the file input element
//            driver.findElement(fileInputLocatortwo).sendKeys(file.getAbsolutePath());
//
//            // Optionally, submit the form or click the upload button if needed
//            // driver.findElement(By.id("upload-button")).click();
//
//            // Perform other actions or assertions here
//        } else {
//            System.out.println("File not found: " + absoluteFilePath);
//        }
//        
////        driver.findElement(By.xpath("//*[@id='VerticalOptions-1-testcase.cr.crf.8.4.option.1']")).click();
////        sleep(2000);
////        
////        driver.findElement(By.xpath("//*[@id='#VerticalOptions-showNoteDiv-2]")).click();
////        sleep(2000);
////        
////        driver.findElement(By.id("VerticalOptions-1-0")).sendKeys("Test");
////        sleep(2000);
////        
////        driver.findElement(By.xpath("//*[@id='VerticalOptions-5-0']")).click();
////        sleep(2000);
//        
//        By fileInputLocators = By.id("my-file-e8d5a052-7b74-4dee-9e81-36774a354768");
//        // Get the current working directory
//        String currentWorkingDirone = System.getProperty("user.dir");
//
//        // Construct the absolute file path
//        String relativeFilePathonethree = "/src/NewPackage/Images/file.png";
//        String absoluteFilePathone = currentWorkingDirone + relativeFilePathonethree;
//        driver.findElement(fileInputLocator).sendKeys(absoluteFilePath);
//        
//        File files = new File(absoluteFilePathone);
//        if (files.exists()) {
//            // Provide the absolute file path to the file input element
//            driver.findElement(fileInputLocators).sendKeys(files.getAbsolutePath());
//
//            // Optionally, submit the form or click the upload button if needed
//            // driver.findElement(By.id("upload-button")).click();
//
//            // Perform other actions or assertions here
//        } else {
//            System.out.println("File not found: " + absoluteFilePath);
//        }
//	        driver.findElement(fileInputLocator).sendKeys("/home/akash/Pictures/Screenshots/Screenshot from 2023-07-21 17-20-37.png");
//       
//        
//        driver.findElement(By.xpath("//*[@id='#TestcaseVertical-handleSaveAndNext']")).click();
//        sleep(2000);
//        
//        driver.quit();
    }

    // Helper method to sleep for a specified duration
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
