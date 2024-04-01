package ru.practicum.shareit.booking.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null)
            return null;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setItemId(booking.getItem().getId());
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
            throw new BadRequestException("При запросе на бронирование вещи с Id " + bookingDto.getItemId() + " даты бронирования указаны не валидно");
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) && status == BookingStatus.WAITING)
            throw new BadRequestException("При запросе на бронирование вещи с Id " + bookingDto.getItemId() + " даты бронирования указаны не валидно");

        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStartDate(bookingDto.getStart());
        booking.setEndDate(bookingDto.getEnd());
        booking.setStatus(status);
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }

    public static List<BookingDto> toBookingDto(List<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings)
            dtos.add(toBookingDto(booking));
        return dtos;
    }
}

