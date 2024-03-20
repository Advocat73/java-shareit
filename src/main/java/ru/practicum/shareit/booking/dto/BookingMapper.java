package ru.practicum.shareit.booking.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@NoArgsConstructor
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null)
            return null;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStart(booking.getStartDate());
        bookingDto.setEnd(booking.getEndDate());
        bookingDto.setStatus(booking.getStatus().toString());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        return bookingDto;
    }

    public static Booking fromBookingDto(BookingDto bookingDto, User booker, Item item, BookingStatus status) {
        if (bookingDto == null)
            return null;
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd()))
            throw new BadRequestException("При запросе на бронирование вещи с Id " +  bookingDto.getItemId() + " даты бронирования указаны не валидно");
        if (bookingDto.getStart().isBefore(LocalDateTime.now()))
            throw new BadRequestException("При запросе на бронирование вещи с Id " +  bookingDto.getItemId() + " даты бронирования указаны не валидно");

        Booking booking = new Booking();
        booking.setStartDate(bookingDto.getStart());
        booking.setEndDate(bookingDto.getEnd());
        booking.setStatus(status);
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }
}

