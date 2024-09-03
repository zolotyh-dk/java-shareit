package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private long id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Email(message = "Email должен быть корректным")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
}
