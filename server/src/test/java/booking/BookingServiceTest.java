package booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.InvalidBookingDateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingServiceImpl bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Test
    public void testBookItem() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание вещи");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        BookingRequest bookingRequest = new BookingRequest(
                savedItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        BookingResponse bookingResponse = bookingService.book(bookingRequest, savedBooker.getId());

        assertThat(bookingResponse.getId(), notNullValue());
        assertThat(bookingResponse.getStatus(), equalTo(BookingStatus.WAITING));

        Booking savedBooking = bookingRepository.findById(bookingResponse.getId()).orElseThrow();
        assertThat(savedBooking.getBooker().getId(), equalTo(savedBooker.getId()));
        assertThat(savedBooking.getItem().getId(), equalTo(savedItem.getId()));
    }

    @Test
    public void testBookItemWithInvalidDates() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание вещи");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingRequest bookingRequest = new BookingRequest(
                item.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(InvalidBookingDateException.class,
                () -> bookingService.book(bookingRequest, savedBooker.getId()));
    }

    @Test
    public void testBookNonExistentItem() {
        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        BookingRequest bookingRequest = new BookingRequest(
                999L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(NotFoundException.class, () -> bookingService.book(bookingRequest, savedBooker.getId()));
    }

    @Test
    public void testUpdateBookingStatus() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание вещи");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        booking.setStart(Instant.now().plusSeconds(3600));
        booking.setEnd(Instant.now().plusSeconds(7200));
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        BookingResponse updatedBooking = bookingService.updateStatus(savedBooking.getId(), true, savedOwner.getId());

        assertThat(updatedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    public void testUpdateBookingStatusByNonOwner() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание вещи");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        booking.setStart(Instant.now().plusSeconds(3600));
        booking.setEnd(Instant.now().plusSeconds(7200));
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        User anotherUser = new User();
        anotherUser.setName("Другой Пользователь");
        anotherUser.setEmail("another@test.com");
        userRepository.save(anotherUser);

        assertThrows(UnauthorizedAccessException.class,
                () -> bookingService.updateStatus(savedBooking.getId(), true, anotherUser.getId()));
    }

    @Test
    public void testGetBookingById() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание вещи");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        booking.setStart(Instant.now().plusSeconds(3600));
        booking.setEnd(Instant.now().plusSeconds(7200));
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        BookingResponse bookingResponse = bookingService.getById(savedBooking.getId(), savedBooker.getId());

        assertThat(bookingResponse.getId(), equalTo(savedBooking.getId()));
        assertThat(bookingResponse.getStatus(), equalTo(savedBooking.getStatus()));
    }

    @Test
    public void testGetAllBookingsByBookerWithDifferentStates() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание вещи");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking pastBooking = new Booking();
        pastBooking.setItem(savedItem);
        pastBooking.setBooker(savedBooker);
        pastBooking.setStart(Instant.now().minusSeconds(7200));
        pastBooking.setEnd(Instant.now().minusSeconds(3600));
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking currentBooking = new Booking();
        currentBooking.setItem(savedItem);
        currentBooking.setBooker(savedBooker);
        currentBooking.setStart(Instant.now().minusSeconds(3600));
        currentBooking.setEnd(Instant.now().plusSeconds(3600));
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(savedItem);
        futureBooking.setBooker(savedBooker);
        futureBooking.setStart(Instant.now().plusSeconds(3600));
        futureBooking.setEnd(Instant.now().plusSeconds(7200));
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        Collection<BookingResponse> pastBookings = bookingService.getByBookerAndState(BookingState.PAST, savedBooker.getId());
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.iterator().next().getId(), equalTo(pastBooking.getId()));

        Collection<BookingResponse> currentBookings = bookingService.getByBookerAndState(BookingState.CURRENT, savedBooker.getId());
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.iterator().next().getId(), equalTo(currentBooking.getId()));

        Collection<BookingResponse> futureBookings = bookingService.getByBookerAndState(BookingState.FUTURE, savedBooker.getId());
        assertThat(futureBookings.size(), equalTo(1));
        assertThat(futureBookings.iterator().next().getId(), equalTo(futureBooking.getId()));
    }

    @Test
    public void testGetAllBookingsByOwnerWithDifferentStates() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание вещи");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking pastBooking = new Booking();
        pastBooking.setItem(savedItem);
        pastBooking.setBooker(savedBooker);
        pastBooking.setStart(Instant.now().minusSeconds(7200));
        pastBooking.setEnd(Instant.now().minusSeconds(3600));
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking currentBooking = new Booking();
        currentBooking.setItem(savedItem);
        currentBooking.setBooker(savedBooker);
        currentBooking.setStart(Instant.now().minusSeconds(3600));
        currentBooking.setEnd(Instant.now().plusSeconds(3600));
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(savedItem);
        futureBooking.setBooker(savedBooker);
        futureBooking.setStart(Instant.now().plusSeconds(3600));
        futureBooking.setEnd(Instant.now().plusSeconds(7200));
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        Collection<BookingResponse> pastBookings = bookingService.getByOwnerAndState(BookingState.PAST, savedOwner.getId());
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.iterator().next().getId(), equalTo(pastBooking.getId()));

        Collection<BookingResponse> currentBookings = bookingService.getByOwnerAndState(BookingState.CURRENT, savedOwner.getId());
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.iterator().next().getId(), equalTo(currentBooking.getId()));

        Collection<BookingResponse> futureBookings = bookingService.getByOwnerAndState(BookingState.FUTURE, savedOwner.getId());
        assertThat(futureBookings.size(), equalTo(1));
        assertThat(futureBookings.iterator().next().getId(), equalTo(futureBooking.getId()));
    }
}
