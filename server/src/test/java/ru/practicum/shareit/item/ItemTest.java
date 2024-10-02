package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemTest {
    @Test
    void testEqualsAndHashCode() {
        User owner = new User();
        owner.setId(1L);

        Item item1 = new Item(1L, "Вещь", "Описание вещи", true, owner, null);
        Item item2 = new Item(1L, "Вещь", "Описание вещи", true, owner, null);
        Item item3 = new Item(2L, "Другая вещь", "Описание другой вещи", true, owner, null);

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1.hashCode(), item3.hashCode());
    }
}
