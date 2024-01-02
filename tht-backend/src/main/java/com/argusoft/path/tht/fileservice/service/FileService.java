package com.argusoft.path.tht.fileservice.service;

import com.argusoft.path.tht.fileservice.FileDetails;
import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    static String RESOURCE_FOLDER;

    public static FileDetails storeFile(MultipartFile multipartFile) throws IOException, InvalidFileTypeException {
        // Validate file type
        String fileName = multipartFile.getOriginalFilename();
        validateFileType(fileName);

        // Construct the path to the resources folder
        Path resourcesPath = Paths.get(RESOURCE_FOLDER);

        // Create the resources folder if it doesn't exist
        if (!Files.exists(resourcesPath)) {
            Files.createDirectories(resourcesPath);
        }

        // Construct the path for the new file
        Path filePath = resourcesPath.resolve(fileName);

        // Write the file to the specified path
        multipartFile.transferTo(filePath.toFile());

        // Return the details of the stored file
        return new FileDetails(filePath.toString(), fileName);
    }

    public static boolean deleteFile(String fileName) {
        Path filePath = Paths.get(RESOURCE_FOLDER, fileName);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void validateFileType(String fileName) throws InvalidFileTypeException {
        // Get the file extension
        String extension = FilenameUtils.getExtension(fileName);

        // Check if the file type is allowed
        if (!("jpeg".equalsIgnoreCase(extension) || "jpg".equalsIgnoreCase(extension) || "png".equalsIgnoreCase(extension))) {
            throw new InvalidFileTypeException("Invalid file type. Only JPEG, JPG, and PNG files are allowed.");
        }
    }

    @Value("${tht-file.location}")
    public void setResourceFolder(String value) {
        RESOURCE_FOLDER = value;
    }

}


