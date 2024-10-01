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

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
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
    public void testGetBookingsByBookerAndState() {
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
        booking.setStart(Instant.now().minusSeconds(7200));
        booking.setEnd(Instant.now().minusSeconds(3600));
        booking.setStatus(BookingStatus.APPROVED);
        Booking savedBooking = bookingRepository.save(booking);

        Collection<BookingResponse> bookings = bookingService.getByBookerAndState(BookingState.PAST, savedBooker.getId());
        assertThat(bookings.size(), equalTo(1));

        BookingResponse bookingResponse = bookings.iterator().next();
        assertThat(bookingResponse.getId(), equalTo(savedBooking.getId()));
        assertThat(bookingResponse.getStatus(), equalTo(savedBooking.getStatus()));
    }
}
