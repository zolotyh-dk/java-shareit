package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingPeriod;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDetailResponse {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingPeriod lastBooking;
    private BookingPeriod nextBooking;
    private List<CommentResponse> comments;
}