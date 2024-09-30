package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findByRequestorIdOrderByCreatedDesc(long requestorId);

    @Query("""
            SELECT r FROM ItemRequest r
            WHERE r.requestor.id <> ?1
            ORDER BY r.created DESC
            """)
    Collection<ItemRequest> findAllExcludingRequestor(long userId);
}
