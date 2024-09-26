package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestResponse toResponseDto(ItemRequest itemRequest) {
        return new ItemRequestResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
    }

    public static ItemRequest toEntity(ItemRequestCreate request, User requestor) {
        final ItemRequest entity = new ItemRequest();
        entity.setDescription(request.getDescription());
        entity.setRequestor(requestor);
        return entity;
    }

    public static ItemRequestResponseWithItems toResponseWithItems(
            ItemRequest itemRequest,
            List<ItemForItemRequest> items) {
        return new ItemRequestResponseWithItems(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                items
        );
    }
}
