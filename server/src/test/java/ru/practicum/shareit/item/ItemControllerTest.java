package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItServer.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    private final ItemWebRequest itemRequest = new ItemWebRequest("Название вещи", "Описание вещи", true, null);
    private final ItemWebResponse itemResponse = new ItemWebResponse(1L, "Название вещи", "Описание вещи", true, null);
    private final ItemDetailResponse itemDetailResponse = new ItemDetailResponse(
            1L, "Название вещи", "Описание вещи", true, null, null, null, Collections.emptyList());
    private final CommentRequest commentRequest = new CommentRequest("Крайне полезная вещь!");
    private final CommentResponse commentResponse = new CommentResponse(1L, "Крайне полезная вещь!", "Юзер Иванович", null);

    @Test
    public void testSaveItem() throws Exception {
        Mockito.when(itemService.save(any(ItemWebRequest.class), eq(1L))).thenReturn(itemResponse);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Mockito.when(itemService.update(any(ItemWebRequest.class), eq(1L), eq(1L))).thenReturn(itemResponse);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));
    }

    @Test
    public void testGetItemById() throws Exception {
        Mockito.when(itemService.getById(1L)).thenReturn(itemDetailResponse);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDetailResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDetailResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDetailResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDetailResponse.getAvailable())));
    }

    @Test
    public void testGetAllItems() throws Exception {
        Mockito.when(itemService.getAll(1L)).thenReturn(List.of(itemDetailResponse));

        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDetailResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDetailResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDetailResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDetailResponse.getAvailable())));
    }

    @Test
    public void testGetItemsByNameOrDescription() throws Exception {
        Mockito.when(itemService.getByNameOrDescription("Item")).thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResponse.getDescription())));
    }

    @Test
    public void testAddComment() throws Exception {
        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(CommentRequest.class))).thenReturn(commentResponse);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName())));
    }
}
