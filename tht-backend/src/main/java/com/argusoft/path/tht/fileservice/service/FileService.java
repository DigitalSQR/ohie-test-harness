package com.argusoft.path.tht.fileservice.service;

import com.argusoft.path.tht.fileservice.FileDetails;
import com.argusoft.path.tht.fileservice.MultipartFileTypeTesterPredicate;
import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.io.InputStream;

@Service
public class FileService {

    static String RESOURCE_FOLDER;

    public static FileDetails storeFile(MultipartFile multipartFile,
                                        MultipartFileTypeTesterPredicate multipartFilePredicateToValidateFile)
            throws IOException, InvalidFileTypeException {

        // Validate file type
        boolean test = multipartFilePredicateToValidateFile.test(multipartFile);
        if(!test){
            throw new InvalidFileTypeException("File Type Validation Failed");
        }
        String fileName = multipartFile.getOriginalFilename();

        // Construct the path to the resources folder
        Path resourcesPath = Paths.get(RESOURCE_FOLDER);

        // Create the resources folder if it doesn't exist
        if (!Files.exists(resourcesPath)) {
            Files.createDirectories(resourcesPath);
        }

        String randomUUID = UUID.randomUUID().toString();

        String extension = FilenameUtils.getExtension(fileName);
        randomUUID = randomUUID +"."+extension;

        // Construct the path for the new file
        Path filePath = resourcesPath.resolve(randomUUID);

        // Write the file to the specified path
        multipartFile.transferTo(filePath.toFile());

        // Return the details of the stored file
        return new FileDetails(filePath.toString(), fileName, randomUUID);
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

    public static byte[] getFileContentByFilePathAndFileName(String filePath,String fileName) throws IOException {
        if(filePath==null){
            filePath = RESOURCE_FOLDER;
        }
        Path path = Paths.get(filePath);
        path = path.resolve(fileName);
        return Files.readAllBytes(path);
    }

    public static String detectInputStreamTypeWithTika(InputStream inputStream) throws IOException {
        Tika tika = new Tika();
        return tika.detect(inputStream);
    }

    public static boolean validateFileType(MultipartFile file, List<String> validateAgainstTypes) throws InvalidFileTypeException, InvalidParameterException, OperationFailedException {
        if(validateAgainstTypes==null || validateAgainstTypes.isEmpty()){
            throw new InvalidParameterException("ValidationAgainstTypes should not be null or empty to validate file type ");
        }

        try {
            String actualType = detectInputStreamTypeWithTika(file.getInputStream());
            if(validateAgainstTypes.contains(actualType)){
                return true;
            }
            throw new InvalidFileTypeException("Given file is of type ("+actualType+") which was not expected in given types => "+(String.join(",", validateAgainstTypes)));
        } catch (IOException e) {
            throw new OperationFailedException("File type validation failed due to an I/O error: " + e.getMessage());
        }
    }


    public static boolean validateFileTypeWithAllowedTypes(MultipartFile file, List<String> allowedTypes)
            throws InvalidFileTypeException, OperationFailedException {

        try {
            return validateFileType(file, allowedTypes);
        } catch (InvalidParameterException e) {
            throw new OperationFailedException("File validation failed due to InvalidParameterException : "+e.getMessage(),e);
        }
    }

    @Value("${tht-file.location}")
    public void setResourceFolder(String value) {
        RESOURCE_FOLDER = value;
    }

}


