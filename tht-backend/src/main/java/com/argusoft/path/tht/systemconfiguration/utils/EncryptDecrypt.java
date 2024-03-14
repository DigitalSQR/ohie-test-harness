package com.argusoft.path.tht.systemconfiguration.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptDecrypt {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private EncryptDecrypt() {
    }

    public static String encryptJson(String json, String encryptionKey) throws Exception {
        // Decode the Base64-encoded encryption key
        byte[] decodedKey = Base64.getDecoder().decode(encryptionKey);
        SecretKeySpec key = new SecretKeySpec(decodedKey, AES_ALGORITHM);

        // Create AES cipher instance
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Encrypt the JSON string
        byte[] encryptedBytes = cipher.doFinal(json.getBytes());

        // Encode the encrypted bytes as Base64
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    public static String decryptJson(String encryptedJson, String encryptionKey) throws Exception {
        // Decode the Base64-encoded encryption key
        byte[] decodedKey = Base64.getDecoder().decode(encryptionKey);
        SecretKeySpec key = new SecretKeySpec(decodedKey, AES_ALGORITHM);

        // Create AES cipher instance
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Decode the Base64-encoded encrypted string
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedJson);

        // Decrypt the encrypted bytes
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Convert decrypted bytes to string
        return new String(decryptedBytes);
    }

    public static String hashString(String string) {
        return passwordEncoder.encode(string);
    }

    public static boolean checkRawString(String rawString, String encodedString) {
        return passwordEncoder.matches(rawString, encodedString);
    }
}
