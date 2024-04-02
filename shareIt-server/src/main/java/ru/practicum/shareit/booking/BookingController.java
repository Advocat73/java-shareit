package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookService;

    @PostMapping
    public BookingDto addNewBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                    @RequestBody BookingDto bookingDto) {
        log.info("BOOKING_КОНТРОЛЛЕР: POST-запрос по эндпоинту /bookings, X-Sharer-User-Id = {}", bookerId);
        return bookService.addNewBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("BOOKING_КОНТРОЛЛЕР: PATCH-запрос по эндпоинту /bookings/{}?approved={}, X-Sharer-User-Id = {}", bookingId, approved, requesterId);
        return bookService.updateBooking(requesterId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                 @PathVariable Long bookingId) {
        log.info("BOOKING_КОНТРОЛЛЕР: GET-запрос по эндпоинту /bookings/{}, X-Sharer-User-Id = {}", bookingId, requesterId);
        return bookService.getBooking(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookerBookings(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @RequestParam(defaultValue = "0", required = false) Integer from,
                                              @RequestParam(defaultValue = "20", required = false) Integer size) {
        log.info("BOOKING_КОНТРОЛЛЕР: GET-запрос по эндпоинту /bookings?state={}&from={}&size={}, X-Sharer-User-Id = {}",
                state.isEmpty() ? "ALL" : state, from, size, requesterId);
        return bookService.getBookerBookings(requesterId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerItemBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state,
                                                 @RequestParam(defaultValue = "0", required = false) Integer from,
                                                 @RequestParam(defaultValue = "20", required = false) Integer size) {
        log.info("BOOKING_КОНТРОЛЛЕР: GET-запрос по эндпоинту /bookings/owner?state={}&from={}&size={}, X-Sharer-User-Id = {}",
                state.isEmpty() ? "ALL" : state, from, size, ownerId);
        return bookService.getOwnerItemBookings(ownerId, state, from, size);
    }
}
