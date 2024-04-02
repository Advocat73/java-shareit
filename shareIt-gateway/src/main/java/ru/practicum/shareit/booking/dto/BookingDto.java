package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    Long id;
    Long itemId;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
    String status;
    UserDto booker;
    ItemDto item;
}
