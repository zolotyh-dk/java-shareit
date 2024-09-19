package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

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

    public static ItemDetailResponse toItemDetailResponse(Item item, BookingPeriod last, BookingPeriod next, List<Comment> comments) {
        return new ItemDetailResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                last,
                next,
                comments
        );
    }

    public static Item requestToItem(ItemRequest request) {
        final Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        return item;
    }
}
