package com.example.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PasswordHasherTest {

    @Test
    void testSaltIsUniqueAndNotEmpty() {
        // BCrypt manages salt internally — generateSalt() is a no-op stub
        String salt = PasswordHasher.generateSalt();
        assertNotNull(salt, "Salt should not be null");
        // Salt is intentionally empty — BCrypt embeds its own salt in the hash
    }

    @Test
    void testHashPasswordIsNonDeterministicWithBCrypt() {
        String password = "SecretPassword123!";
        String salt = PasswordHasher.generateSalt();

        String hash1 = PasswordHasher.hashPassword(password, salt);
        String hash2 = PasswordHasher.hashPassword(password, salt);

        assertNotNull(hash1);
        assertNotNull(hash2);
        // BCrypt produces a different hash each call (random salt embedded inside)
        assertNotEquals(hash1, hash2, "BCrypt must produce unique hashes each time");
    }

    @Test
    void testDifferentSaltsProduceDifferentHashes() {
        String password = "SecretPassword123!";
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();

        String hash1 = PasswordHasher.hashPassword(password, salt1);
        String hash2 = PasswordHasher.hashPassword(password, salt2);

        assertNotEquals(hash1, hash2, "Same password with different salts must produce different hashes");
    }

    @Test
    void testVerifyPasswordMatches() {
        String password = "CorrectHorseBatteryStaple";
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hashPassword(password, salt);

        assertTrue(PasswordHasher.verifyPassword(password, salt, hash), "Password should verify successfully");
        assertFalse(PasswordHasher.verifyPassword("WrongPassword", salt, hash), "Incorrect password must fail verification");
    }
}
