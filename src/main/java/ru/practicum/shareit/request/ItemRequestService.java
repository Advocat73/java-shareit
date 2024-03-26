package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewRequestItem(Long requesterId, ItemRequestDto itemRequestDto);

    List<GetItemRequestDto> getRequesterItems(Long requesterId);

    List<GetItemRequestDto>  getAllItemRequestsFromIndexPageable(Long requesterId, Integer firstIndex, Integer size);

    GetItemRequestDto getItemRequest(Long requesterId, Long requestId);
}
