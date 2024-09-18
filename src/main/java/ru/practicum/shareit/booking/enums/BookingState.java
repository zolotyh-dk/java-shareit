package ru.practicum.shareit.booking.enums;

public enum BookingState {
    ALL,        // Все бронирования
    CURRENT,    // Текущие бронирования
    PAST,       // Завершённые бронирования
    FUTURE,     // Будущие бронирования
    WAITING,    // Ожидающие подтверждения
    REJECTED    // Отклонённые
}
