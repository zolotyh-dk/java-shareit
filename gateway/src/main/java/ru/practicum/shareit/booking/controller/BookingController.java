package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingState;

import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> book(@Valid @RequestBody BookingRequest body,
								@RequestHeader(X_SHARER_USER_ID) long bookerId) {
		log.info("Получен запрос POST /bookings на бронирование аренды {}", body);
		ResponseEntity<Object> response = bookingClient.book(bookerId, body);
		log.info("В ответ на запрос POST /bookings возвращаем бронирование {}", response);
		return response;
	}


	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateStatus(@PathVariable long bookingId,
										@RequestParam boolean approved,
										@RequestHeader(X_SHARER_USER_ID) long userId) {
		log.info("Получен запрос на подтверждение аренды PATCH /bookings/{}?approved={}", bookingId, approved);
		ResponseEntity<Object> response = bookingClient.updateStatus(bookingId, approved, userId);
		log.info("В ответ на запрос PATCH /bookings/{}?approved={} возвращаем бронирование {}", bookingId, approved, response);
		return response;
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@PathVariable long bookingId, @RequestHeader(X_SHARER_USER_ID) long userId) {
		log.info("Получен запрос на получение бронирования GET /bookings/{} от пользователя с id={}", bookingId, userId);
		ResponseEntity<Object> response = bookingClient.getById(userId, bookingId);
		log.info("В ответ на запрос GET /bookings/{} возвращаем бронирование {}", bookingId, response);
		return response;
	}

	@GetMapping
	public ResponseEntity<Object> getByBookerAndState(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
														   @RequestHeader(X_SHARER_USER_ID) long bookerId) {
		log.info("Получен запрос на получение бронирований GET /bookings?state={} для пользователя id={}", stateParam, bookerId);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Неизвестный статус бронирования: " + stateParam));
		ResponseEntity<Object> bookings = bookingClient.getByBookerAndState(state, bookerId);
		log.info("В ответ на запрос GET /bookings?state={} возвращаем бронирования {}", state, bookings);
		return bookings;
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getByOwnerAndState(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
														  @RequestHeader(X_SHARER_USER_ID) long ownerId) {
		log.info("Получен запрос на получение бронирований GET /bookings/owner?state={} для пользователя id={}", stateParam, ownerId);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Неизвестный статус бронирования: " + stateParam));
		ResponseEntity<Object> bookings = bookingClient.getByOwnerAndState(state, ownerId);
		log.info("В ответ на запрос GET /bookings?state={} возвращаем бронирования {}", state, bookings);
		return bookings;
	}
}
