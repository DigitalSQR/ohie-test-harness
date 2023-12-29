package com.argusoft.path.tht.usermanagement.models.dto;


public class UpdatePasswordInfo {

    private String oldPassword;

    private String newPassword;

    private String base64UserEmail;

    private String base64TokenId;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getBase64UserEmail() {
        return base64UserEmail;
    }

    public void setBase64UserEmail(String base64UserEmail) {
        this.base64UserEmail = base64UserEmail;
    }

    public String getBase64TokenId() {
        return base64TokenId;
    }

    public void setBase64TokenId(String base64TokenId) {
        this.base64TokenId = base64TokenId;
    }

    public void trimObject() {
        this.setBase64TokenId(this.getBase64TokenId().trim());
        this.setBase64UserEmail(this.getBase64UserEmail().trim());
        this.setOldPassword(this.getOldPassword().trim());
        this.setNewPassword(this.getNewPassword().trim());
    }
}
