package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository{
    private final Map<Long, List<Item>> items = new HashMap<>();

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
        return null;
    }

    @Override
    public Item save(Long userId, Item item) {
        item.setId(getId());
        items.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item update(Long userId, Item item) {
        Item oldItem = findItemByUserId(userId, item.getId());
        if (oldItem != null) {
            oldItem.setName(item.getName());
            oldItem.setDescription(item.getDescription());
            oldItem.setAvailable(item.isAvailable());
        }
        return  oldItem;
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        if(items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId().equals(itemId));
        }
    }

    private Long getId() {
        Long lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}

