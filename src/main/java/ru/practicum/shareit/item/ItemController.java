package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ITEM_КОНТРОЛЛЕР: GET-запрос по эндпоинту /items, X-Sharer-User-Id = {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: GET-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItemBySubstring(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam String text) {
        log.info("ITEM_КОНТРОЛЛЕР: SEARCH-запрос по эндпоинту /items/search?{}, X-Sharer-User-Id = {}", text, userId);
        return itemService.searchItemBySubstring(text);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @Valid @RequestBody Item item) {
        log.info("ITEM_КОНТРОЛЛЕР: POST-запрос по эндпоинту /items, X-Sharer-User-Id = {}", userId);
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody Item item,
                       @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: PATCH-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        item.setId(itemId);
        return itemService.updateItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("ITEM_КОНТРОЛЛЕР: DELETE-запрос по эндпоинту /items/{}, X-Sharer-User-Id = {}", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }
}
