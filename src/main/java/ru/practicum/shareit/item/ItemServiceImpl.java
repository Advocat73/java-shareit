package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<Item> getItems(Long userId) {
        return itemRepository.findByUserId(userId);
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        return itemRepository.findItemByUserId(userId, itemId);
    }

    @Override
    public Item addNewItem(Long userId, Item item) {
        //item.setUserId(userId);
        return itemRepository.save(userId, item);
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        if (!userId.equals(item.getOwner().getId()))
            return null;
        return itemRepository.update(userId, item);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}

