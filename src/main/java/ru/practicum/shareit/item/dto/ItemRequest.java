package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Data
@AllArgsConstructor
public class ItemRequest {
    @NotBlank(groups = {Create.class}, message = "Название вещи не может быть пустым")
    @Size(max = 255, groups = {Create.class, Update.class}, message = "Название не может превышать 255 символов")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Описание вещи не может быть пустым")
    @Size(max = 1000, groups = {Create.class, Update.class}, message = "Описание не может превышать 1000 символов")
    private String description;

    @NotNull(groups = {Create.class}, message = "Доступность вещи не может быть null")
    private Boolean available;

    private long requestId;
}
