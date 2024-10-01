package request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestWebRequest;
import ru.practicum.shareit.request.dto.ItemRequestWebResponse;
import ru.practicum.shareit.request.dto.ItemRequestWebResponseWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = ShareItServer.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private ObjectMapper mapper;

    private final ItemRequestWebRequest itemRequestWebRequest = new ItemRequestWebRequest("Горный велосипед");
    private final ItemRequestWebResponse itemRequestWebResponse = new ItemRequestWebResponse(
            1L,
            "Горный велосипед",
            LocalDateTime.now()
    );

    private final ItemRequestWebResponseWithItems itemRequestWebResponseWithItems = new ItemRequestWebResponseWithItems(
            1L,
            "Горный велосипед",
            LocalDateTime.now(),
            Collections.emptyList()
    );

    @Test
    public void testSave() throws Exception {
        Mockito.when(requestService.save(any(ItemRequestWebRequest.class), eq(1L))).thenReturn(itemRequestWebResponse);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestWebRequest))
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWebResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWebResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }

    @Test
    public void testGetAllByRequestor() throws Exception {
        Mockito.when(requestService.getAllByRequestor(eq(1L))).thenReturn(Collections.singletonList(itemRequestWebResponseWithItems));

        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestWebResponseWithItems.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestWebResponseWithItems.getDescription())))
                .andExpect(jsonPath("$[0].created", is(notNullValue())))
                .andExpect(jsonPath("$[0].items", is(empty())));
    }

    @Test
    public void testGetAll() throws Exception {
        Mockito.when(requestService.getAll(eq(1L))).thenReturn(Collections.singletonList(itemRequestWebResponse));

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestWebResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestWebResponse.getDescription())))
                .andExpect(jsonPath("$[0].created", is(notNullValue())));
    }

    @Test
    public void testGetRequestById() throws Exception {
        Mockito.when(requestService.getRequestById(eq(1L), eq(1L))).thenReturn(itemRequestWebResponseWithItems);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWebResponseWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWebResponseWithItems.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.items", is(empty())));
    }
}
