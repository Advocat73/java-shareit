package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    public List<Item> getItems(Long userId);

    public Item getItem(Long userId, Long itemId);

    public List<Item> searchItemBySubstring(String subStr);

    public Item addNewItem(Long userId, Item item);

    public Item updateItem(Long userId, Item item);


    public void deleteItem(Long userId, Long itemId);
}

