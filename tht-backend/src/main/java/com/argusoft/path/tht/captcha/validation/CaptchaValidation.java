package com.argusoft.path.tht.captcha.validation;

import com.argusoft.path.tht.captcha.models.CaptchaInfo;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CaptchaValidation {

    public static List<ValidationResultInfo> validateCaptchaEmpty(String code,String encryptedString,
                                                      ContextInfo contextInfo) throws InvalidParameterException {


        List<ValidationResultInfo> errors = new ArrayList<>();

        //trim
        trimCaptcha(code);
        trimCaptcha(encryptedString);

        //check required fields;
        ValidationUtils.validateNotEmpty(code, "captcha", errors);
        ValidationUtils.validateNotEmpty(encryptedString,"encrypted String",errors);
        return errors;
    }

    public static void trimCaptcha(String code){
        if (code != null) {
           code.trim();
        }
    }

    public static void validateCaptcha(String code, Date expiryTime, String userCode, ContextInfo contextInfo,List<ValidationResultInfo> errors){
        ValidationUtils.validateNotEmpty(code,"actual code",errors);
        ValidationUtils.validateRequired(expiryTime,"expiry time",errors);
        ValidationUtils.validateNotEmpty(userCode,"User code",errors);

        Date currentTime = new Date();

        if(!expiryTime.before(currentTime)){
            if(!code.equals(userCode)){
                errors.add(new ValidationResultInfo("code", ErrorLevel.ERROR, "Invalid captcha."));
            }
        }
        else{
            errors.add(new ValidationResultInfo("code", ErrorLevel.ERROR, "Captcha expired."));
        }
    }


}
