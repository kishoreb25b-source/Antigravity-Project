package com.example.repository;

import com.example.config.DatabaseConfig;
import com.example.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserRepository {

    private static final String INSERT_SQL = 
            "INSERT INTO users (name, email, phone_number, password_hash, password_salt, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String FIND_BY_EMAIL_SQL = 
            "SELECT id, name, email, phone_number, password_hash, password_salt, created_at FROM users WHERE email = ?";

    private static final String EXISTS_BY_EMAIL_SQL = 
            "SELECT 1 FROM users WHERE email = ? LIMIT 1";

    // Saves a new user and populates the generated ID and creation timestamp
    public User save(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email cannot be blank");
        }
        if (user.getPasswordHash() == null || user.getPasswordSalt() == null) {
            throw new IllegalArgumentException("Password hash and salt must be present");
        }

        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail().trim().toLowerCase());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getPasswordSalt());
            stmt.setTimestamp(6, timestamp);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    user.setCreatedAt(now);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }

        return user;
    }

    // Check if an email already exists in the system
    public boolean existsByEmail(String email) throws SQLException {
        if (email == null || email.isBlank()) {
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_EMAIL_SQL)) {
            stmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Find a user by their email address
    public Optional<User> findByEmail(String email) throws SQLException {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {
            stmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

                    User user = new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("password_hash"),
                            rs.getString("password_salt"),
                            createdAt
                    );
                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }
}
