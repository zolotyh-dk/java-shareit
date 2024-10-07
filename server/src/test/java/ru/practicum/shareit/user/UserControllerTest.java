package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = ShareItServer.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    private final UserRequest userRequest = new UserRequest("Пользователь", "user@test.com");

    private final UserResponse userResponse = new UserResponse(1L, "Пользователь", "user@test.com");

    @Test
    public void testSaveUser() throws Exception {
        Mockito.when(userService.save(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @Test
    public void testUpdateUser() throws Exception {
        Mockito.when(userService.update(any(UserRequest.class), eq(1L))).thenReturn(userResponse);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @Test
    public void testGetUserById() throws Exception {
        Mockito.when(userService.getById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).delete(1L);
    }
}
