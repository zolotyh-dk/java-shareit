package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {
    @Test
    void testEqualsAndHashCode() {
        User requestor = new User();
        requestor.setId(1L);
        
        ItemRequest request1 = new ItemRequest(1L, "Запрос на вещь", requestor, Instant.now());
        ItemRequest request2 = new ItemRequest(1L, "Запрос на вещь", requestor, Instant.now());
        ItemRequest request3 = new ItemRequest(2L, "Другой запрос", requestor, Instant.now());

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }
}
