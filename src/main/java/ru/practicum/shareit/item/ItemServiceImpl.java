package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    public final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу на получение вещей пользователя с id {}", userId);
        return itemRepository.findByUserId(userId).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на получение вещи с id {}", userId, itemId);
        
        return itemMapper.toItemDto(itemRepository.findItem(itemId));
    }

    @Override
    public List<ItemDto> searchItemBySubstring(String subStr) {
        if (subStr == null)
            throw new BadRequestException("Нужна строка для поиска");
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу на поиск вещи, содержащей текст {}", subStr);
        return itemRepository.searchItemBySustring(subStr).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        if (userId == null)
            throw new BadRequestException("Не указан Id пользователя при запросе Update");
        if (userService.getUser(userId) == null)
            throw new NotFoundException("Нет пользователя с ID: " + userId);
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на добавление новой вещи", userId);
        Item item = itemMapper.fromItemDto(itemDto);
        item.setOwnerId(userId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        if (itemId == null)
            throw new NotFoundException("Не указан Id вещи при запросе Update");
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на изменение вещи с id {}", userId, itemId);
        Item item = itemMapper.fromItemDto(itemDto);
        return itemMapper.toItemDto(itemRepository.update(userId, item, itemId));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на удаление вещи с id {}", userId, itemId);
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}

