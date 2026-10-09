package com.example.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConfigTest {

    @Test
    void testConfigurationLoadsFromProperties() {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();

        assertNotNull(url, "Database URL should not be null");
        assertTrue(url.startsWith("jdbc:mysql:"), "Database URL must start with jdbc:mysql:");
        assertNotNull(user, "Database user should not be null");
    }
}
