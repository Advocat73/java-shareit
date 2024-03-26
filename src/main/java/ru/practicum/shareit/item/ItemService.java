package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesBookingDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    List<ItemWithDatesBookingDto> getUserItems(Long ownerId, Integer from, Integer size);

    ItemWithDatesBookingDto getItem(Long userId, Long itemId);

    List<ItemDto> searchItemBySubstring(String subStr);

    void deleteItem(Long userId, Long itemId);

    CommentDto addNewComment(Long userId, Long itemId, CommentDto commentDto);
}

