package com.tht.test.utiliy;

        import java.awt.AWTException;
        import java.awt.Rectangle;
        import java.awt.Robot;
        import java.awt.Toolkit;
        import java.awt.image.BufferedImage;
        import java.io.File;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import javax.imageio.ImageIO;

        import com.itextpdf.io.image.ImageData;
        import com.itextpdf.io.image.ImageDataFactory;
        import com.itextpdf.kernel.pdf.PdfDocument;
        import com.itextpdf.kernel.pdf.PdfWriter;
        import com.itextpdf.layout.Document;
        import com.itextpdf.layout.element.Image;
        import com.itextpdf.layout.element.Paragraph;

public class ScreenShot {

    private static List<String> screenshotPaths = new ArrayList<>();
    private static final String BASE_PATH = "/srv/tht/test/";

    public static void createScreenshot(String name) {
        try {
            // Create a robot instance
            Robot robot = new Robot();
            // Capture the whole screen
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            // Save captured image to a file
            String screenshotPath = BASE_PATH + name + ".png";
            File screenshotFile = new File(screenshotPath);
            // Ensure the directories exist
            screenshotFile.getParentFile().mkdirs();
            ImageIO.write(screenFullImage, "png", screenshotFile);
            // Add the screenshot path to the list
            screenshotPaths.add(screenshotPath);
            System.out.println("Screenshot saved as: " + screenshotFile.getAbsolutePath());
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
    }

    public static void createPdfFromScreenshots(String pdfName) {
        try {
            if (!screenshotPaths.isEmpty()) {
                String pdfPath = BASE_PATH + pdfName + ".pdf";
                PdfWriter writer = new PdfWriter(pdfPath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                for (String screenshotPath : screenshotPaths) {
                    ImageData imageData = ImageDataFactory.create(screenshotPath);
                    Image image = new Image(imageData);
                    document.add(image);
                    // Add image name below the image
                    String imageName = new File(screenshotPath).getName().replaceFirst("[.][^.]+$", "");
                    Paragraph imageNameParagraph = new Paragraph(imageName);
                    document.add(imageNameParagraph);
                }
                document.close();
                System.out.println("PDF created at: " + pdfPath);
            } else {
                System.out.println("No screenshots taken.");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}