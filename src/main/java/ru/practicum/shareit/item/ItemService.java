package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> searchItemBySubstring(String subStr);

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);


    void deleteItem(Long userId, Long itemId);
}

