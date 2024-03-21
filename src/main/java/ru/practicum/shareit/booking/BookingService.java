package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(Long bookerId, BookingDto bookingDto);

    BookingDto updateBooking(Long requesterId, Long bookingId, Boolean isApproved);

    BookingDto getBooking(Long requesterId, Long bookingId);

    List<BookingDto> getBookerBookings(Long requesterId, String state, Integer from, Integer size);

    List<BookingDto> getOwnerItemBookings(Long ownerId, String state, Integer from, Integer size);
}
