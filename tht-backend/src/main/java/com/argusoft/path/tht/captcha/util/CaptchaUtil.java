package com.argusoft.path.tht.captcha.util;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class CaptchaUtil {

    //create captcha object
    public static Captcha createCaptcha(int width, int height){
        Color startColor = new Color(173, 216, 230); // Light Blue color
        Color endColor = new Color(240, 248, 255);   // Another shade of Light Blue color
        return new Captcha.Builder(width, height)
                .addBackground(new GradiatedBackgroundProducer(startColor, endColor))
                .addText()
                .addNoise()
                .build();
    }

    //convert to binary string
    public static String encodeBase64(Captcha captcha){
        String imageData = null;

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(captcha.getImage(), "png", outputStream);
            byte[] array = Base64.getEncoder().encode(outputStream.toByteArray());
            imageData = new String(array);

        }catch (Exception e){
            e.printStackTrace();
        }
        return imageData;
    }

}
