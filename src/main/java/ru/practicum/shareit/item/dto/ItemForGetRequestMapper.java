package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

@NoArgsConstructor
public class ItemForGetRequestMapper {
    public static ItemForGetRequestDto toItemForGetRequestDto(Item item) {
        if (item == null)
            return null;
        ItemForGetRequestDto itemForGetRequestDto = new ItemForGetRequestDto();
        itemForGetRequestDto.setId(item.getId());
        itemForGetRequestDto.setName(item.getName());
        itemForGetRequestDto.setDescription(item.getDescription());
        itemForGetRequestDto.setRequestId(item.getRequest().getId());
        itemForGetRequestDto.setAvailable(item.getAvailable());
        return itemForGetRequestDto;
    }
}
