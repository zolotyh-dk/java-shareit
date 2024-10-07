package booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookingRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
public class BookingRequestJsonTest {

    @Autowired
    private JacksonTester<BookingRequest> json;

    @Test
    void testBookingRequest() throws Exception {
        BookingRequest bookingRequest = new BookingRequest(
                1L,
                LocalDateTime.of(2024, 10, 1, 10, 0),
                LocalDateTime.of(2024, 10, 1, 12, 0)
        );

        JsonContent<BookingRequest> result = json.write(bookingRequest);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-10-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-10-01T12:00:00");
    }
}
