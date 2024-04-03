package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@NotNull(message = "Не указан Id пользователя при запросе на добавление вещи")
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("GATEWAY: Creating item {}, by user id {}", itemDto.getName(), userId);
        return itemClient.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@NotNull(message = "Не указан Id пользователя при запросе на изменение данных вещи")
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto,
                                             @NotNull(message = "Не указан Id вещи при запросе Update")
                                             @PathVariable Long itemId) {
        log.info("GATEWAY: Updating item {}", itemId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @Valid @PositiveOrZero(message = "Индекс первой страницы не может быть отрицательным")
                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                               @Valid @Positive(message = "Количество ответов на запрос на странице должно быть положительным")
                                               @RequestParam(defaultValue = "1", required = false) Integer size) {
        log.info("GATEWAY: Getting items of user {}", ownerId);
        return itemClient.getUserItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@NotNull(message = "Не указан Id пользователя при запросе на получение вещи")
                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @NotNull(message = "Не указан Id вещи при запросе на получение вещи")
                                          @PathVariable Long itemId) {
        log.info("GATEWAY: Getting item {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemBySubstring(@NotNull(message = "Не указан Id пользователя при запросе на поиск вещи")
                                                        @RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @NotNull(message = "Не указан текст для поиска при запросе на поиск вещи")
                                                        @RequestParam String text) {
        log.info("GATEWAY: Searching by text {}", text);
        return itemClient.searchItemBySubstring(userId, text);
    }


    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@NotNull(message = "Не указан Id пользователя при запросе на удаление вещи")
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @NotNull(message = "Не указан Id вещи при запросе на удаление вещи")
                                             @PathVariable Long itemId) {
        log.info("GATEWAY: Removing item {}", itemId);
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@NotNull(message = "Не указан Id пользователя при запросе на добавления комментария о вещи")
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @NotNull(message = "Не указан Id вещи при запросе на добавления комментария о вещи")
                                                @PathVariable Long itemId) {
        log.info("GATEWAY: Creating comment for item {}", itemId);
        return itemClient.addNewComment(userId, itemId, commentDto);
    }
}
