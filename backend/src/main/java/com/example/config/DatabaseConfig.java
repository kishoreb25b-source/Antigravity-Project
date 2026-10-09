package com.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static final String DEFAULT_PROPERTIES = "application.properties";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Warning: " + DEFAULT_PROPERTIES + " not found on classpath.");
            }
        } catch (IOException e) {
            System.err.println("Error loading " + DEFAULT_PROPERTIES + ": " + e.getMessage());
        }
    }

    public static String getUrl() {
        String env = System.getenv("DB_URL");
        return (env != null && !env.isBlank()) ? env : properties.getProperty("db.url", "jdbc:mysql://localhost:3306/project2_db");
    }

    public static String getUser() {
        String env = System.getenv("DB_USER");
        return (env != null && !env.isBlank()) ? env : properties.getProperty("db.user", "root");
    }

    public static String getPassword() {
        String env = System.getenv("DB_PASSWORD");
        return (env != null) ? env : properties.getProperty("db.password", "");
    }

    // Opens a plain JDBC connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }
}
