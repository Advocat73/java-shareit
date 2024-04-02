package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("GATEWAY: Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.addNewBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@NotNull(message = "Не указан Id пользователя при запросе на изменение бронирования")
                                                @RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                @NotNull(message = "Не указан Id бронирования при запросе на изменение бронирования")
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("GATEWAY: Updating booking {}", bookingId);
        return bookingClient.updateBooking(requesterId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("GATEWAY: Getting booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerItemBookings(@NotNull(message = "Не указан Id пользователя при запросе на получение бронирования своих вещей")
                                                       @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                       @Valid @PositiveOrZero(message = "Индекс первой страницы не может быть отрицательным")
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Valid @Positive(message = "Количество ответов на запрос о бронированиях на странице должно быть положительным")
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GATEWAY:  Getting bookings of items of owner {} by state {}", ownerId, stateParam);
        return bookingClient.getOwnerItemBookings(ownerId, state, from, size);
    }
}