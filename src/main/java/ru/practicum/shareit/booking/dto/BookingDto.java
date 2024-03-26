package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    Long id;
    @NotNull
    Long itemId;
    Long bookerId;
    @NotNull
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
    String status;
    User booker;
    ItemDto item;
}
