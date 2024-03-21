package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

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

    /*public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto, User requester) {
        if (itemRequestDto == null)
            return null;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }*/
}
