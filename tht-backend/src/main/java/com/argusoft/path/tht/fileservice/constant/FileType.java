package com.argusoft.path.tht.fileservice.constant;

/**
 * This enum provides contract for fileType
 *
 * @author Hardik
 */

public enum FileType {

    //pdf file
    APPLICATION_PDF("application/pdf", "PDF"),
    //image png file
    IMAGE_PNG("image/png", "PNG"),
    //image jpeg file
    IMAGE_JPEG("image/jpeg", "JPEG"),
    //zip file
    ZIP("application/zip","ZIP"),

    //MP4 file
    QUICKTIMEVIDEO("video/quicktime","MP4"),

    //MP4 file
    MP4("video/mp4","MP4");

    private final String type;

    private final String name;

    FileType(String fileType, String name) {
        this.type = fileType;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
