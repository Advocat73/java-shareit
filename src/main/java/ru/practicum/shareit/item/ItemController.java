package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@NotNull(message = "Не указан Id пользователя при запросе на добавление вещи")
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("ITEM_КОНТРОЛЛЕР: POST-запрос по эндпоинту /items, X-Sharer-User-Id = {}", userId);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@NotNull(message = "Не указан Id пользователя при запросе на изменение данных вещи")
                            @RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody ItemDto itemDto,
                            @NotNull(message = "Не указан Id вещи при запросе Update")
                            @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: PATCH-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemWithDatesBookingDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @Valid @PositiveOrZero(message = "Индекс первой страницы не может быть отрицательным")
                                                      @RequestParam(defaultValue = "0", required = false) Integer from,
                                                      @Valid @Positive(message = "Количество ответов на запрос на странице должно быть положительным")
                                                      @RequestParam(defaultValue = "1", required = false) Integer size) {
        log.info("ITEM_КОНТРОЛЛЕР: GET-запрос по эндпоинту /items?from={}&size={}, X-Sharer-User-Id = {}", ownerId, from, size);
        return itemService.getUserItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemWithDatesBookingDto getItem(@NotNull(message = "Не указан Id пользователя при запросе на получение вещи")
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @NotNull(message = "Не указан Id вещи при запросе на получение вещи")
                                           @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: GET-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemBySubstring(@NotNull(message = "Не указан Id пользователя при запросе на поиск вещи")
                                               @RequestHeader("X-Sharer-User-Id") Long userId,
                                               @NotNull(message = "Не указан текст для поиска при запросе на поиск вещи")
                                               @RequestParam String text) {
        log.info("ITEM_КОНТРОЛЛЕР: SEARCH-запрос по эндпоинту /items/search?text={}, X-Sharer-User-Id = {}", text, userId);
        return itemService.searchItemBySubstring(text);
    }


    @DeleteMapping("/{itemId}")
    public void deleteItem(@NotNull(message = "Не указан Id пользователя при запросе на удаление вещи")
                           @RequestHeader("X-Sharer-User-Id") Long userId,
                           @NotNull(message = "Не указан Id вещи при запросе на удаление вещи")
                           @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: DELETE-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@NotNull(message = "Не указан Id пользователя при запросе на добавления комментария о вещи")
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @NotNull(message = "Не указан Id вещи при запросе на добавления комментария о вещи")
                                    @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: POST-запрос по эндпоинту /items/{}/comment, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.addNewComment(userId, itemId, commentDto);
    }
}
