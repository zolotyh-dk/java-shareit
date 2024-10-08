package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemWebResponse {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
