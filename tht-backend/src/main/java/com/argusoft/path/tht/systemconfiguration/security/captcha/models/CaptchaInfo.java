package com.argusoft.path.tht.systemconfiguration.security.captcha.models;

/**
 * This info is for Captcha DTO
 *
 * @author Ishita
 */

public class CaptchaInfo {

    private String image;

    private String captcha;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
