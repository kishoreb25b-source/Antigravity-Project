package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.model.User;

public class UserRepositoryTest {

    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepository();
    }

    @Test
    void testSaveNullUserThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void testSaveUserWithBlankEmailThrowsException() {
        User user = new User("Jane Doe", "  ", "1234567890", "hash", "salt");
        assertThrows(IllegalArgumentException.class, () -> repository.save(user));
    }

    @Test
    void testSaveUserWithoutPasswordSecurityThrowsException() {
        User user = new User("Jane Doe", "jane@example.com", "1234567890", null, null);
        assertThrows(IllegalArgumentException.class, () -> repository.save(user));
    }

    @Test
    void testExistsByEmailWithBlankReturnsFalse() throws Exception {
        assertFalse(repository.existsByEmail(""));
        assertFalse(repository.existsByEmail(null));
    }

    @Test
    void testFindByEmailWithBlankReturnsEmpty() throws Exception {
        assertTrue(repository.findByEmail("").isEmpty());
        assertTrue(repository.findByEmail(null).isEmpty());
    }
}
