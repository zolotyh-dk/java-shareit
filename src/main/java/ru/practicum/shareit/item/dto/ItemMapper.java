package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.time.Instant;

public class ItemMapper {
    public static ItemResponse toItemResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDetailResponse toItemDetailResponse(Item item, Instant start, Instant end) {
        return new ItemDetailResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                start,
                end
        );
    }

    public static Item requestToItem(ItemRequest request) {
        final Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        return item;
    }

    public static Item responseToItem(ItemResponse response) {
        final Item item = new Item();
        item.setId(response.getId());
        item.setName(response.getName());
        item.setDescription(response.getDescription());
        item.setAvailable(response.getAvailable());
        return item;
    }
}
