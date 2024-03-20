package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

public class ItemWithDatesBookingMapper {
    public static ItemWithDatesBookingDto toItemWithDatesBookingDto(Item item) {
        return (item == null) ? null : (ItemWithDatesBookingDto) ItemMapper.toItemDto(new ItemWithDatesBookingDto(), item);
    }
}
