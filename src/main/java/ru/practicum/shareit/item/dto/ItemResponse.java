package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemResponse {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<Comment> comments;
}
