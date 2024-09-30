package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemResponse toItemResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public ItemDetailResponse toItemDetailResponse(Item item, BookingPeriod last, BookingPeriod next, List<Comment> comments) {
        return new ItemDetailResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                last,
                next,
                comments.stream().map(CommentMapper::toCommentResponse).toList()
        );
    }

    public Item toItem(ItemRequest request, ru.practicum.shareit.request.model.ItemRequest itemRequest) {
        final Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setRequest(itemRequest);
        return item;
    }

    public ItemForItemRequest toItemForItemRequest(Item item) {
        return new ItemForItemRequest(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
