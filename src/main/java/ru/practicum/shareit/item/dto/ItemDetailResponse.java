package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ItemDetailResponse {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Instant startDate;
    private Instant endDate;
}