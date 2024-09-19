package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Поиск по пользователю, забронировавшему вещь
    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = ?1 AND b.start <= ?2 AND b.end >= ?2
        ORDER BY b.start DESC
        """)
    List<Booking> findByBookerIdCurrentBookingsOrderByStartDesc(long bookerId, Instant now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = ?1 AND b.start > ?2
        ORDER BY b.start DESC
        """)
    List<Booking> findByBookerIdFutureBookingsOrderByStartDesc(long bookerId, Instant now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = ?1 AND b.end < ?2
        ORDER BY b.start DESC
        """)
    List<Booking> findByBookerIdPastBookingsOrderByStartDesc(long bookerId, Instant now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    //Поиск по владельцу вещи
    @Query("""
       SELECT b FROM Booking b
       WHERE b.item.owner.id = ?1 AND b.start <= ?2 AND b.end >= ?2
       ORDER BY b.start DESC
       """)
    List<Booking> findByOwnerIdCurrentBookingsOrderByStartDesc(long ownerId, Instant now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = ?1 AND b.start > ?2
        ORDER BY b.start DESC
        """)
    List<Booking> findByOwnerIdFutureBookingsOrderByStartDesc(long ownerId, Instant now);

    @Query("""
       SELECT b FROM Booking b
       WHERE b.item.owner.id = ?1 AND b.end < ?2
       ORDER BY b.start DESC
       """)
    List<Booking> findByOwnerIdPastBookingsOrderByStartDesc(long ownerId, Instant now);

    @Query("""
       SELECT b FROM Booking b
       WHERE b.item.owner.id = ?1 AND b.status = ?2
       ORDER BY b.start DESC
       """)
    List<Booking> findByOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = ?1
        ORDER BY b.start DESC
        """)
    List<Booking> findAllByOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemIdOrderByStartDesc(long itemId);
}
