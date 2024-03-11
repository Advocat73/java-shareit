package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                          @Valid @RequestBody BookingDto bookingDto) {
        log.info("BOOKING_КОНТРОЛЛЕР: POST-запрос по эндпоинту /bookings, X-Sharer-User-Id = {}", bookerId);
        return bookService.addNewBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long requesterId,
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
    public List<BookingDto> getBookersBookings(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("BOOKING_КОНТРОЛЛЕР: GET-запрос по эндпоинту /bookings?{}, X-Sharer-User-Id = {}", state.isEmpty() ? "ALL" : state, requesterId);
        return bookService.getBookings(requesterId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("BOOKING_КОНТРОЛЛЕР: GET-запрос по эндпоинту /bookings/owner, X-Sharer-User-Id = {}", ownerId);
        return bookService.getOwnerItemBookings(ownerId, state);
    }
}
