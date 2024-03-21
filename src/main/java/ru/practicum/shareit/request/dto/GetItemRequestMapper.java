package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

public class GetItemRequestMapper {
    public static GetItemRequestDto toGetItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null)
            return null;
        GetItemRequestDto getItemRequestDto = new GetItemRequestDto();
        getItemRequestDto.setId(itemRequest.getId());
        getItemRequestDto.setDescription(itemRequest.getDescription());
        getItemRequestDto.setCreated(itemRequest.getCreated());
        return getItemRequestDto;
    }
}
