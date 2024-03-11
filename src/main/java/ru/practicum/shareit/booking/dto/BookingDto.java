package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validator.DateAfterNow;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDto {
    Long Id;
    @NotNull
    Long itemId;
    Long bookerId;
    @NotNull
    //@DateAfterNow
    LocalDateTime start;
    @NotNull
    //@DateAfterNow
    LocalDateTime end;
    String status;
    User booker;
    ItemDto item;
}
