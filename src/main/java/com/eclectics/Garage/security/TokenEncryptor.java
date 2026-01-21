package com.eclectics.Garage.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets; // <-- NEW IMPORT

public class TokenEncryptor {

    private static final String SECRET = "12345678901234567890123456789012";

    public static String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "AES"); // Use UTF-8 for key
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Use explicit padding/mode
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            // --- FIX 1: Use UTF-8 for String to Bytes ---
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting token", e);
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "AES"); // Use UTF-8 for key
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Use explicit padding/mode
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);

            // --- FIX 2: Use UTF-8 for Bytes to String ---
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error decrypting token", e);
        }
    }
}