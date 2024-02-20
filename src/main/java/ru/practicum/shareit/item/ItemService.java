package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    Item getItem(Long userId, Long itemId);

    List<Item> searchItemBySubstring(String subStr);

    Item addNewItem(Long userId, Item item);

    Item updateItem(Long userId, ItemDto itemDto);


    void deleteItem(Long userId, Long itemId);
}

