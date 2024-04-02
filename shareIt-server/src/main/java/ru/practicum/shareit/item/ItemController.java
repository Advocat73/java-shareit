package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesBookingDto;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("ITEM_КОНТРОЛЛЕР: POST-запрос по эндпоинту /items, X-Sharer-User-Id = {}", userId);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody ItemDto itemDto,
                            @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: PATCH-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemWithDatesBookingDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @RequestParam(defaultValue = "0", required = false) Integer from,
                                                      @RequestParam(defaultValue = "1", required = false) Integer size) {
        log.info("ITEM_КОНТРОЛЛЕР: GET-запрос по эндпоинту /items?from={}&size={}, X-Sharer-User-Id = {}", ownerId, from, size);
        return itemService.getUserItems(ownerId, from, size);
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
        log.info("ITEM_КОНТРОЛЛЕР: SEARCH-запрос по эндпоинту /items/search?text={}, X-Sharer-User-Id = {}", text, userId);
        return itemService.searchItemBySubstring(text);
    }


    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: DELETE-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: POST-запрос по эндпоинту /items/{}/comment, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.addNewComment(userId, itemId, commentDto);
    }
}
