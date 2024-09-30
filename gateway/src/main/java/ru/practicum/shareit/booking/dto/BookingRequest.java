package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingRequest {
    @NotNull(message = "Идентификатор вещи не может быть null")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования не может быть null")
    @FutureOrPresent(message = "Дата начала бронирования не должна быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;
}
