package com.example.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHasher {

    private static final int COST = 12; // BCrypt work factor (OWASP recommended minimum)

    // BCrypt embeds the salt inside the hash — no separate salt needed
    public static String generateSalt() {
        // BCrypt handles salt internally; return empty string for compatibility
        return "";
    }

    // Hash password with BCrypt
    public static String hashPassword(String password, String salt) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }

    // Verify password against stored BCrypt hash
    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        if (password == null || expectedHash == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), expectedHash);
        return result.verified;
    }
}
