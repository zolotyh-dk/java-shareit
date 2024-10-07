package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    @Test
    void testEqualsAndHashCode() {
        User author = new User();
        author.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Comment comment1 = new Comment(1L, "Отличная вещь!", item, author, Instant.now());
        Comment comment2 = new Comment(1L, "Отличная вещь!", item, author, Instant.now());
        Comment comment3 = new Comment(2L, "Отличная вещь!", item, author, Instant.now());

        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
        assertEquals(comment1.hashCode(), comment2.hashCode());
        assertNotEquals(comment1.hashCode(), comment3.hashCode());
    }
}
