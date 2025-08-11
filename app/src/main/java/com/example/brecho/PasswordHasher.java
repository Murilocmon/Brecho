package com.example.brecho;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";

    public static String hashPassword(String password) {
        if (password == null) { // Adicionar verificação de nulo
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] encodedhash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Error hashing password, algorithm not found.", e);
        }
    }

    public static boolean verifyPassword(String originalPassword, String storedHash) {
        if (originalPassword == null || storedHash == null) {
            return false;
        }
        String newHash = hashPassword(originalPassword);
        return newHash != null && newHash.equals(storedHash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}