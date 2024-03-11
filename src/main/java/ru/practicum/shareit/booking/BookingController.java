package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookService;

    @PostMapping
    public BookingDto addNewBooking(@NotNull(message = "Не указан Id пользователя при запросе на добавления бронирования")
                                    @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        log.info("BOOKING_КОНТРОЛЛЕР: POST-запрос по эндпоинту /bookings, X-Sharer-User-Id = {}", bookerId);
        return bookService.addNewBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@NotNull(message = "Не указан Id пользователя при запросе на изменение бронирования")
                                    @RequestHeader("X-Sharer-User-Id") Long requesterId,
                                    @NotNull(message = "Не указан Id бронирования при запросе на изменение бронирования")
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("BOOKING_КОНТРОЛЛЕР: PATCH-запрос по эндпоинту /bookings/{}?approved={}, X-Sharer-User-Id = {}", bookingId, approved, requesterId);
        return bookService.updateBooking(requesterId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@NotNull(message = "Не указан Id пользователя при запросе на получение бронирования")
                                 @RequestHeader("X-Sharer-User-Id") Long requesterId,
                                 @NotNull(message = "Не указан Id бронирования при запросе на получение бронирования")
                                 @PathVariable Long bookingId) {
        log.info("BOOKING_КОНТРОЛЛЕР: GET-запрос по эндпоинту /bookings/{}, X-Sharer-User-Id = {}", bookingId, requesterId);
        return bookService.getBooking(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookerBookings(@NotNull(message = "Не указан Id пользователя при запросе на получение бронирования")
                                              @RequestHeader("X-Sharer-User-Id") Long requesterId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("BOOKING_КОНТРОЛЛЕР: GET-запрос по эндпоинту /bookings?{}, X-Sharer-User-Id = {}", state.isEmpty() ? "ALL" : state, requesterId);
        return bookService.getBookerBookings(requesterId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnersItemBookings(@NotNull(message = "Не указан Id пользователя при запросе на получение бронирования своих вещей")
                                                  @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("BOOKING_КОНТРОЛЛЕР: GET-запрос по эндпоинту /bookings/owner, X-Sharer-User-Id = {}", ownerId);
        return bookService.getOwnerItemBookings(ownerId, state);
    }
}
