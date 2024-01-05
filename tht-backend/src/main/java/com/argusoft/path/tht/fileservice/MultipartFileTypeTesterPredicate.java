package com.argusoft.path.tht.fileservice;

import com.argusoft.path.tht.fileservice.service.FileService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Predicate;

public class MultipartFileTypeTesterPredicate implements Predicate<MultipartFile> {

    private final List<String> allowedTypes;

    public MultipartFileTypeTesterPredicate(List<String> allowedTypes) throws OperationFailedException {
        if(allowedTypes.isEmpty()){
            throw new OperationFailedException("ValidationAgainstTypes should not be null or empty to validate file type ");
        }
        this.allowedTypes = allowedTypes;
    }

    @Override
    public boolean test(MultipartFile multipartFile) {
        try {
            return FileService.validateFileTypeWithAllowedTypes(multipartFile,allowedTypes);
        } catch (OperationFailedException e) {
            throw new RuntimeException("File validation failed due to OperationFailedException : "+e.getMessage(),e);
        }
    }

}
