package com.argusoft.path.tht.usermanagement.models.dto;

/**
 * This info is for Reset password DTO.
 *
 * @author Rohit
 */

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
        if(oldPassword != null){
            this.setOldPassword(this.getOldPassword().trim());
        }
        if(newPassword != null){
            this.setNewPassword(this.getNewPassword().trim());
        }
    }
}
