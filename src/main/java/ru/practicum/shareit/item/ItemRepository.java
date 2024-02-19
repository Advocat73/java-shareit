package ru.practicum.shareit.item;

import java.util.List;

interface ItemRepository {

    List<Item> findByUserId(Long userId);

    Item findItemByUserIdAndItemId(Long userId, Long itemId);

    Item findItem(Long itemId);

    List<Item> searchItemBySustring(String subStr);

    Item save(Item item);

    Item update(Long userId, Item item);

    void deleteByUserIdAndItemId(Long userId, Long itemId);
}
