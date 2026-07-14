package com.isdemirstaj.backend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    // Düz metin şifreyi (Örn: "123") alıp SHA-256 formatına çevirir
    public static String toSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString(); // 64 karakterli şifreli metin döndürür
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Şifreleme algoritması bulunamadı!", e);
        }
    }
}