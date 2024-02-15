package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @Valid @RequestBody Item item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody Item item) {
        return itemService.updateItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
