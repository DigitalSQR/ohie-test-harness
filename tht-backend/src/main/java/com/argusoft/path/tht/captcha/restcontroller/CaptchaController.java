package com.argusoft.path.tht.captcha.restcontroller;

import com.argusoft.path.tht.captcha.models.CaptchaInfo;
import com.argusoft.path.tht.captcha.service.CaptchaService;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("")
    public CaptchaInfo getCaptcha(@RequestAttribute("contextInfo") ContextInfo contextInfo) throws Exception {
        return captchaService.createCaptcha(contextInfo);
    }

    @PostMapping("/validate")
    public List<ValidationResultInfo> validateCaptcha(@RequestHeader("captchaCode") String captchaCode,
                                                       @RequestHeader("captcha") String captcha,
                                                       @RequestAttribute("contextInfo") ContextInfo contextInfo) throws Exception {
       return captchaService.validateCaptcha(captchaCode,captcha,contextInfo);
    }

}
