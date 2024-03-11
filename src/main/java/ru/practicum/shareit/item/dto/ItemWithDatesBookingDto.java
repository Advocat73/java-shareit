package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
public class ItemWithDatesBookingDto extends ItemDto {
    BookingDto lastBooking;
    BookingDto nextBooking;
}
