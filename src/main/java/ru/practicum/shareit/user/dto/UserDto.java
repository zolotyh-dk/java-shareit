package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private long id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Email(message = "Email должен быть корректным")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
