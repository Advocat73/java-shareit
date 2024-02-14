package ru.practicum.shareit.item;

import java.util.List;

interface ItemRepository {

    List<Item> findByUserId(Long userId);

    Item findItemByUserId(Long userId, Long itemId);

    Item save(Long userId, Item item);

    Item update(Long userId, Item item);

    void deleteByUserIdAndItemId(Long userId, Long itemId);
}
