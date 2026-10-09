package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTest {

    private RegistrationService service;

    @BeforeEach
    void setUp() {
        service = new RegistrationService();
    }

    @Test
    void testRegisterWithInvalidNameFails() {
        var result = service.register("A", "valid@example.com", "1234567890", "password123");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Name must be at least 2 characters"));
    }

    @Test
    void testRegisterWithInvalidEmailFails() {
        var result = service.register("Alice", "not-an-email", "1234567890", "password123");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("valid email"));
    }

    @Test
    void testRegisterWithShortPasswordFails() {
        var result = service.register("Alice", "alice@example.com", "1234567890", "short");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Password must be at least 8 characters"));
    }
}
