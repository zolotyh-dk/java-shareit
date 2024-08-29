package ru.practicum.shareit.item.dto;

public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private Long requestId;

    public ItemDto(String name, String description, boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
