package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Data
@AllArgsConstructor
public class UserRequest {
    @NotBlank(groups = {Create.class}, message = "Имя не должно быть пустым")
    @Size(max = 100, groups = {Create.class, Update.class})
    private String name;

    @Email(groups = {Create.class, Update.class}, message = "Email должен быть корректным")
    @NotBlank(groups = {Create.class}, message = "Email не должен быть пустым")
    @Size(max = 512, groups = {Create.class, Update.class}, message = "Email не должен превышать 512 символов")
    private String email;
}
