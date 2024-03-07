package com.argusoft.path.tht.systemconfiguration.security.captcha.service;

import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.captcha.models.CaptchaInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.nimbusds.jose.shaded.json.JSONObject;

import java.util.Date;
import java.util.List;

public interface CaptchaService {

    public CaptchaInfo createCaptcha(ContextInfo contextInfo) throws Exception;

    public void setupCaptcha(CaptchaInfo captchaInfo) throws Exception;

    public String encryptCodeAndExpiryTime(String code, Date expiryTime) throws Exception;

    public JSONObject decryptCodeTime(String captcha) throws Exception;

    public List<ValidationResultInfo> validateCaptcha(String captchaCode,String captcha,ContextInfo contextInfo) throws Exception;

}
