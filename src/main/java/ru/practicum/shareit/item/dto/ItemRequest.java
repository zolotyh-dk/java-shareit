package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemRequest {
    // И при save и при update id не передается в теле запроса.
    // При update id получаем из URL как PathVariable
    // Поэтому отдельные CreateItemRequest и UpdateItemRequest делать пока не стал
    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull (message = "Доступность вещи не может быть null")
    private Boolean available;

    private Long requestId;
}
