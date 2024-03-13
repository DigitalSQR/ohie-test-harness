package com.argusoft.path.tht.usermanagement.models.dto;

public class ResetPasswordInfo {

    private String oldPassword;

    private String newPassword;

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

    public void trimObject() {
        this.setOldPassword(this.getOldPassword().trim());
        this.setNewPassword(this.getNewPassword().trim());
    }
}
