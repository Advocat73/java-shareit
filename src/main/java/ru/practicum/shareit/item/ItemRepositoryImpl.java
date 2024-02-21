package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();
    long itemCounter = 0;

    @Override
    public List<Item> findByUserId(Long userId) {
        log.info("ITEM_ХРАНИЛИЩЕ: Получение вещей пользователя с id {}", userId);
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item findItemByUserIdAndItemId(Long userId, Long itemId) {
        log.info("ITEM_ХРАНИЛИЩЕ: Получение вещи с id {} пользователем с id {}", itemId, userId);
        List<Item> items = findByUserId(userId);
        for (Item item : items) {
            if (item.getId().equals(itemId))
                return item;
        }
        return null;
    }

    @Override
    public Item findItem(Long itemId) {
        log.info("ITEM_ХРАНИЛИЩЕ: Получение вещи с id {}", itemId);
        for (Map.Entry<Long, List<Item>> itemSet : items.entrySet())
            for (Item item : itemSet.getValue())
                if (item.getId().equals(itemId))
                    return item;
        throw new NotFoundException("Нет вещи с Id: " + itemId);
    }

    @Override
    public List<Item> searchItemBySustring(String subStr) {
        log.info("ITEM_ХРАНИЛИЩЕ: Получение вещей по сабстрингу {}", subStr);
        return (!subStr.isEmpty()) ?
            items.values().stream().flatMap(Collection::stream)
                .filter(item->item.getName().toLowerCase().contains(subStr.toLowerCase())
                        || item.getDescription().toLowerCase().contains(subStr.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList()) : List.of();
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        log.info("ITEM_ХРАНИЛИЩЕ: Добавление пользователем с id {} вещи, которой присвоен номер {}", item.getOwnerId(), item.getId());
        items.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item update(Long userId, Item item, Long itemId) {
        Item updatedItem = findItemByUserIdAndItemId(userId, itemId);
        if (updatedItem == null)
            throw new NotFoundException("У пользователя с Id " + userId + " нет вещи с Id: " + itemId);
        if (!userId.equals(updatedItem.getOwnerId()))
            throw new BadRequestException("Пользователь с Id " + userId + " не является собственником вещи с Id " + itemId);
        log.info("ITEM_ХРАНИЛИЩЕ: Изменение данных вещи с id {} пользователем с id {}", itemId, userId);
        if (item.getName() != null)
            updatedItem.setName(item.getName());
        if (item.getDescription() != null)
            updatedItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            updatedItem.setAvailable(item.getAvailable());
        return updatedItem;
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        log.info("ITEM_ХРАНИЛИЩЕ: Удаление вещи с id {} пользователем с id {}", itemId, userId);
        if (items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId().equals(itemId));
        } else throw new NotFoundException("У пользователя с Id " + userId + " нет вещи с Id: " + itemId);
    }

    private Long getId() {
        return ++itemCounter;
    }
}

