package com.example.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    @Test
    void testSaltIsUniqueAndNotEmpty() {
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();

        assertNotNull(salt1, "Salt 1 should not be null");
        assertNotNull(salt2, "Salt 2 should not be null");
        assertFalse(salt1.isBlank(), "Salt should not be blank");
        assertNotEquals(salt1, salt2, "Two generated salts must be unique");
    }

    @Test
    void testHashPasswordIsDeterministicWithSameSalt() {
        String password = "SecretPassword123!";
        String salt = PasswordHasher.generateSalt();

        String hash1 = PasswordHasher.hashPassword(password, salt);
        String hash2 = PasswordHasher.hashPassword(password, salt);

        assertNotNull(hash1);
        assertEquals(hash1, hash2, "Hashing same password with same salt must yield identical hash");
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
