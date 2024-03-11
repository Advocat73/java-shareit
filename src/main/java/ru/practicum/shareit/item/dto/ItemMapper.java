package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@NoArgsConstructor
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return (item == null) ? null : _toItemDto(new ItemDto(), item);
    }

    public static Item fromItemDto(ItemDto itemDto, User owner) {
        if (itemDto == null)
            return null;
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        return item;
    }


    protected static ItemDto _toItemDto(ItemDto itemDto, Item item) {
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }
}
