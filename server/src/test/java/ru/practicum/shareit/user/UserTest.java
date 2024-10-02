package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void testEqualsAndHashCode() {
        User user1 = new User(1L, "Иван Иванов", "ivan.ivanov@example.com");
        User user2 = new User(1L, "Иван Иванов", "ivan.ivanov@example.com");
        User user3 = new User(2L, "Петр Петров", "petr.petrov@example.com");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }
}
