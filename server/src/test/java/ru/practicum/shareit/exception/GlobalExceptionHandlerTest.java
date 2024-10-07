package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundExceptions() {
        NotFoundException exception = new NotFoundException("Ресурс не найден");

        ResponseEntity<Object> response = exceptionHandler.handleNotFoundExceptions(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ресурс не найден", ((GlobalExceptionHandler.ErrorDetails) response.getBody()).error());
    }

    @Test
    void handleEmailAlreadyExistsExceptions() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("Email уже существует");

        ResponseEntity<Object> response = exceptionHandler.handleEmailAlreadyExistsExceptions(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email уже существует", ((GlobalExceptionHandler.ErrorDetails) response.getBody()).error());
    }

    @Test
    void handleUnauthorizedAccessException() {
        UnauthorizedAccessException exception = new UnauthorizedAccessException("Доступ для пользователя отклонен");

        ResponseEntity<Object> response = exceptionHandler.handleUnauthorizedAccessException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Доступ для пользователя отклонен", ((GlobalExceptionHandler.ErrorDetails) response.getBody()).error());
    }

    @Test
    void handleItemNotAvailableException() {
        ItemNotAvailable exception = new ItemNotAvailable("Вещь недоступна");

        ResponseEntity<Object> response = exceptionHandler.handleItemNotAvailableException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Вещь недоступна", ((GlobalExceptionHandler.ErrorDetails) response.getBody()).error());
    }

    @Test
    void handleInvalidBookingDateException() {
        InvalidBookingDateException exception = new InvalidBookingDateException("Невалидная дата аренды");

        ResponseEntity<Object> response = exceptionHandler.handleItemNotAvailableException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Невалидная дата аренды", ((GlobalExceptionHandler.ErrorDetails) response.getBody()).error());
    }
}
