package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

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
        if (userService.getUser(userId) == null)
            throw new NotFoundException("Нет пользователя с ID: " + userId);
        item.setOwnerId(userId);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        if (item.getId() == null)
            throw new NotFoundException("Не указан Id вещи при запросе Update");
        return itemRepository.update(userId, item);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}

