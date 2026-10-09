package com.example.service;

import java.sql.SQLException;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.PasswordHasher;

public class RegistrationService {

    private final UserRepository userRepository;

    public RegistrationService() {
        this(new UserRepository());
    }

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static class RegistrationResult {
        private final boolean success;
        private final String message;
        private final User user;

        public RegistrationResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }

    public RegistrationResult register(String name, String email, String phoneNumber, String password) {
        if (name == null || name.trim().length() < 2) {
            return new RegistrationResult(false, "Name must be at least 2 characters.", null);
        }
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
            return new RegistrationResult(false, "A valid email address is required.", null);
        }
        if (phoneNumber == null || phoneNumber.trim().length() < 7) {
            return new RegistrationResult(false, "Phone number must be at least 7 digits.", null);
        }
        if (password == null || password.length() < 8) {
            return new RegistrationResult(false, "Password must be at least 8 characters.", null);
        }

        String normalizedEmail = email.trim().toLowerCase();

        try {
            // Duplicate email check
            if (userRepository.existsByEmail(normalizedEmail)) {
                return new RegistrationResult(false, "An account with this email already exists.", null);
            }

            // Cryptographic salt generation & password hashing (PBKDF2)
            String salt = PasswordHasher.generateSalt();
            String passwordHash = PasswordHasher.hashPassword(password, salt);

            // Persistence
            User newUser = new User(name.trim(), normalizedEmail, phoneNumber.trim(), passwordHash, salt);
            User savedUser = userRepository.save(newUser);

            return new RegistrationResult(true, "Registration successful!", savedUser);

        } catch (SQLException e) {
            return new RegistrationResult(false, "Database error: " + e.getMessage(), null);
        }
    }
}
