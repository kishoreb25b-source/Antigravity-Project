package com.example.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int ITERATIONS = 65536; // OWASP recommended iteration count
    private static final int KEY_LENGTH = 256;   // 256 bits output hash
    private static final int SALT_BYTES = 16;    // 128 bits salt

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Generate cryptographically secure random salt
    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash password with salt using PBKDF2-HMAC-SHA512
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt cannot be null");
        }

        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Error during password hashing: " + e.getMessage(), e);
        }
    }

    // Constant-time verification to prevent timing side-channel attacks
    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        if (password == null || salt == null || expectedHash == null) {
            return false;
        }

        String calculatedHash = hashPassword(password, salt);
        byte[] calculatedBytes = Base64.getDecoder().decode(calculatedHash);
        byte[] expectedBytes = Base64.getDecoder().decode(expectedHash);

        return MessageDigest.isEqual(calculatedBytes, expectedBytes);
    }
}
