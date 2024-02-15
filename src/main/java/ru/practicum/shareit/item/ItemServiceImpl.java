package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<Item> getItems(Long userId) {
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу на получение вещей пользователя с id {}", userId);
        return itemRepository.findByUserId(userId);
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на получение вещи с id {}", userId, itemId);
        return itemRepository.findItem(itemId);
    }

    @Override
    public List<Item> searchItemBySubstring(String subStr) {
        if (subStr == null)
            throw new BadRequestException("Нужна строка для поиска");
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу на поиск вещи, содержащей текст {}", subStr);
        return itemRepository.searchItemBySustring(subStr);
    }

    @Override
    public Item addNewItem(Long userId, Item item) {
        if (userService.getUser(userId) == null)
            throw new NotFoundException("Нет пользователя с ID: " + userId);
        item.setOwnerId(userId);
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на добавление новой вещи", userId);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        if (item.getId() == null)
            throw new NotFoundException("Не указан Id вещи при запросе Update");
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на изменение вещи с id {}", userId, item.getId());
        return itemRepository.update(userId, item);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на удаление вещи с id {}", userId, itemId);
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}

