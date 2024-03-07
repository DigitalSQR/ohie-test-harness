package com.argusoft.path.tht.fileservice.service;

import com.argusoft.path.tht.fileservice.constant.FileType;
import com.argusoft.path.tht.fileservice.models.dto.FileDetails;
import com.argusoft.path.tht.fileservice.predicate.MultipartFileTypeTesterPredicate;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidFileTypeException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    public static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    static String RESOURCE_FOLDER;

    public static FileDetails storeFile(MultipartFile multipartFile,
                                        MultipartFileTypeTesterPredicate multipartFilePredicateToValidateFile)
            throws IOException, InvalidFileTypeException {

        // Validate file type
        boolean test = multipartFilePredicateToValidateFile.test(multipartFile);
        if (!test) {
            LOGGER.error(ValidateConstant.INVALID_FILE_TYPE_EXCEPTION + FileService.class.getSimpleName());
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
        randomUUID = randomUUID + "." + extension;

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
            LOGGER.error(ValidateConstant.IO_EXCEPTION + FileService.class.getSimpleName(), e);
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] getFileContentByFilePathAndFileName(String filePath, String fileName) throws IOException {
        if (filePath == null) {
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

    public static boolean validateFileType(MultipartFile file, Set<FileType> validateAgainstTypes) throws InvalidFileTypeException, InvalidParameterException, OperationFailedException {
        if (validateAgainstTypes == null || validateAgainstTypes.isEmpty()) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION+ FileService.class.getSimpleName());
            throw new InvalidParameterException("ValidationAgainstTypes should not be null or empty to validate file type ");
        }

        try {
            String actualType = detectInputStreamTypeWithTika(file.getInputStream());
            if (validateAgainstTypes.stream().anyMatch(allowedFileType -> allowedFileType.getType().equals(actualType))) {
                return true;
            }
            LOGGER.error(ValidateConstant.INVALID_FILE_TYPE_EXCEPTION + FileService.class.getSimpleName());
            throw new InvalidFileTypeException("Invalid file type, only allowed file types are : " + (validateAgainstTypes.stream().map(FileType::getName).collect(Collectors.joining(", "))));
        } catch (IOException e) {
            LOGGER.error(ValidateConstant.IO_EXCEPTION + FileService.class.getSimpleName(), e);
            throw new OperationFailedException("File type validation failed due to an I/O error: " + e.getMessage());
        }
    }

    public static boolean validateFileTypeWithAllowedTypes(MultipartFile file, Set<FileType> allowedTypes)
            throws InvalidFileTypeException, OperationFailedException {

        try {
            return validateFileType(file, allowedTypes);
        } catch (InvalidParameterException e) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION+ FileService.class.getSimpleName(), e);
            throw new OperationFailedException("File validation failed due to InvalidParameterException : " + e.getMessage(), e);
        }
    }

    @Value("${tht-file.location}")
    public void setResourceFolder(String value) {
        RESOURCE_FOLDER = value;
    }


}


