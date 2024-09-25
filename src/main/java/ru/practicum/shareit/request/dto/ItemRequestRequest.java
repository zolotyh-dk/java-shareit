package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemRequestRequest {
    @NotBlank(message = "Описание запроса вещи не может быть пустым")
    @Size(max = 1000, message = "Описание не может превышать 1000 символов")
    String description;
}
