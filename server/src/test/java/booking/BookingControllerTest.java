package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItServer.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    private final BookingRequest bookingRequest = new BookingRequest(
            1L,
            LocalDateTime.of(2024, 10, 1, 10, 0, 0),
            LocalDateTime.of(2024, 10, 1, 12, 0, 0)
    );

    private final BookingResponse bookingResponse = new BookingResponse(
            1L,
            LocalDateTime.of(2024, 10, 1, 10, 0, 0),
            LocalDateTime.of(2024, 10, 1, 12, 0, 0),
            new ItemResponse(1L, "Название вещи", "Описание вещи", true, null),
            new UserResponse(1L, "Пользователь", "user@test.com"),
            BookingStatus.APPROVED
    );

    @Test
    public void testBook() throws Exception {
        Mockito.when(bookingService.book(any(BookingRequest.class), eq(1L))).thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponse.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Mockito.when(bookingService.updateStatus(eq(1L), eq(true), eq(1L))).thenReturn(bookingResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponse.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())));
    }

    @Test
    public void testGetById() throws Exception {
        Mockito.when(bookingService.getById(eq(1L), eq(1L))).thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponse.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())));
    }

    @Test
    public void testGetByBookerAndState() throws Exception {
        Mockito.when(bookingService.getByBookerAndState(any(BookingState.class), eq(1L)))
                .thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponse.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponse.getStatus().toString())));
    }

    @Test
    public void testGetByOwnerAndState() throws Exception {
        Mockito.when(bookingService.getByOwnerAndState(any(BookingState.class), eq(1L)))
                .thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponse.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponse.getStatus().toString())));
    }
}
