package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestResponseDto toResponseDto(ItemRequest entity) {
        return new ItemRequestResponseDto(
                entity.getId(),
                entity.getDescription(),
                entity.getCreated().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
    }

    public static ItemRequest toEntity(ItemRequestCreateDto dto, User requestor) {
        final ItemRequest entity = new ItemRequest();
        entity.setDescription(dto.getDescription());
        entity.setRequestor(requestor);
        return entity;
    }
}
