package com.argusoft.path.tht.captcha.service.impl;

import cn.apiclub.captcha.Captcha;
import com.argusoft.path.tht.captcha.models.CaptchaInfo;
import com.argusoft.path.tht.captcha.service.CaptchaService;
import com.argusoft.path.tht.captcha.util.CaptchaUtil;
import com.argusoft.path.tht.captcha.util.EncryptDecrypt;
import com.argusoft.path.tht.captcha.validation.CaptchaValidation;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final String key="3a985da74fe225d6c9a29e5a9e4c1b93";

    @Override
    public CaptchaInfo createCaptcha(ContextInfo contextInfo) throws Exception {
        CaptchaInfo captchaInfo = new CaptchaInfo();
        this.setupCaptcha(captchaInfo);
        return captchaInfo;
    }

    @Override
    public void setupCaptcha(CaptchaInfo captchaInfo) throws Exception {
        Captcha captcha = CaptchaUtil.createCaptcha(300,80);
        captchaInfo.setImage(CaptchaUtil.encodeBase64(captcha));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        String code=encryptCodeAndExpiryTime(captcha.getAnswer(), calendar.getTime());
        captchaInfo.setCaptcha(code);
    }

    @Override
    public String encryptCodeAndExpiryTime(String code, Date expiryTime) throws Exception {
        //converting them into string
        String codeAndExpiryTime =jsonToString(code,expiryTime);
        // encrypt
        return EncryptDecrypt.encryptJson(codeAndExpiryTime,key);
    }

    @Override
    public JSONObject decryptCodeTime(String captcha) throws Exception {
        // decrypt
        String codeAndExpiryTime = EncryptDecrypt.decryptJson(captcha,key);
        //Convert string into Json
        return stringToJson(codeAndExpiryTime);
    }

    @Override
    public List<ValidationResultInfo> validateCaptcha(String captchaCode,String captcha,ContextInfo contextInfo) throws Exception {
        // checking if input not empty
        List<ValidationResultInfo> errors = CaptchaValidation.validateCaptchaEmpty(captchaCode,captcha,contextInfo);
        if(ValidationUtils.containsErrors(errors, ErrorLevel.ERROR))
        {
            return errors;
        }
        JSONObject codeTimeObject = decryptCodeTime(captcha);

        // Define the date format matching the format of the expiry time string
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        // Parse the expiry time string into a Date object

        Date expiryTime = dateFormat.parse(codeTimeObject.get("expiryTime").toString());
        CaptchaValidation.validateCaptcha(
                codeTimeObject.get("code").toString(),
                expiryTime,
                captchaCode,
                contextInfo,
                errors);
        return errors;
    }

    public String jsonToString(String code, Date expiryTime){
        // Create a JSON object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("expiryTime", expiryTime);
        // Convert JSON object to string
        return jsonObject.toString();
    }

    public JSONObject stringToJson(String codeAndTime){

        // Create an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // Create a JSON object
        JSONObject jsonObject = new JSONObject();
        try {
            // Convert the JSON string to a JsonNode
            JsonNode jsonNode = objectMapper.readTree(codeAndTime);
            // Access fields in the JsonNode
            String code = jsonNode.get("code").asText();
            String expiryTime = jsonNode.get("expiryTime").asText();

            jsonObject.put("code",code);
            jsonObject.put("expiryTime", expiryTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
