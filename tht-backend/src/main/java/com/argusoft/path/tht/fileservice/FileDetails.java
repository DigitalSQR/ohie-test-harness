package com.argusoft.path.tht.fileservice;

public class FileDetails {

    private final String location;
    private final String fileName;

    public FileDetails(String location, String fileName) {
        this.location = location;
        this.fileName = fileName;
    }

    public String getLocation() {
        return location;
    }

    public String getFileName() {
        return fileName;
    }

}
