package com.argusoft.path.tht.fileservice.models.dto;

/**
 * This info is for files.
 *
 * @author Hardik
 */

public class FileDetails {

    private final String location;
    private final String fileName;

    private final String fileId;

    public FileDetails(String location, String fileName, String fileId) {
        this.location = location;
        this.fileName = fileName;
        this.fileId = fileId;
    }

    public String getLocation() {
        return location;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileId() {
        return fileId;
    }
}
