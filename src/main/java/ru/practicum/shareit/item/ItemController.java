package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@NotNull(message = "Не указан Id пользователя при запросе на добавление вещи")
                       @RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        log.info("ITEM_КОНТРОЛЛЕР: POST-запрос по эндпоинту /items, X-Sharer-User-Id = {}", userId);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@NotNull(message = "Не указан Id пользователя при запросе на изменение данных вещи")
                          @RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @NotNull(message = "Не указан Id вещи при запросе Update")
                          @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: PATCH-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemWithDatesBookingDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ITEM_КОНТРОЛЛЕР: GET-запрос по эндпоинту /items, X-Sharer-User-Id = {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithDatesBookingDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: GET-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemBySubstring(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam String text) {
        log.info("ITEM_КОНТРОЛЛЕР: SEARCH-запрос по эндпоинту /items/search?{}, X-Sharer-User-Id = {}", text, userId);
        return itemService.searchItemBySubstring(text);
    }


    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: DELETE-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto add(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                       @NotNull @PathVariable Long itemId,
                       @Valid @RequestBody CommentDto commentDto) {
        log.info("ITEM_КОНТРОЛЛЕР: POST-запрос по эндпоинту /items/{}/comment, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.addNewComment(userId, itemId, commentDto);
    }
}
