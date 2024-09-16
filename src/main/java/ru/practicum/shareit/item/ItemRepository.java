package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findByOwnerId(Long ownerId);

    @Query("""
            SELECT i FROM Item i
            WHERE i.available = TRUE
            AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))
            OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))
            """)
    Collection<Item> searchByNameOrDescription(String text);
}
