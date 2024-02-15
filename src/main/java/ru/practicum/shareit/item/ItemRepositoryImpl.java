package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository{
    private final Map<Long, List<Item>> items = new HashMap<>();
    long itemCounter = 0;

    @Override
    public List<Item> findByUserId(Long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item findItemByUserId(Long userId, Long itemId) {
        List<Item> items = findByUserId(userId);
        for (Item item : items) {
            if (item.getId().equals(itemId))
                return item;
        }
        throw new NotFoundException("Нет вещи с ID: " + itemId);
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item update(Long userId, Item item) {
        Item updatedItem = findItemByUserId(userId, item.getId());
        if (!userId.equals(updatedItem.getOwnerId()))
            throw new BadRequestException("Пользователь с Id " + userId + " не является собственником вещи с Id " + item.getId());
        if (item.getName() != null)
            updatedItem.setName(item.getName());
        if (item.getDescription() != null)
            updatedItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            updatedItem.setAvailable(item.getAvailable());
        return  updatedItem;
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        if(items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId().equals(itemId));
        }
    }

    private Long getId() {
        return ++itemCounter;
    }
}

