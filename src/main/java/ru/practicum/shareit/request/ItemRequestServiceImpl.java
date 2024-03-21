package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForGetRequestDto;
import ru.practicum.shareit.item.dto.ItemForGetRequestMapper;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addNewItem(Long requesterId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + requesterId));
        log.info("REQUESTS_СЕРВИС: Отправлен запрос к хранилищу на добавление запроса");
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(ItemRequestMapper.fromItemRequestDto(itemRequestDto, requester)));
    }

    @Override
    public List<GetItemRequestDto> getRequesterItems(Long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + requesterId));
        log.info("REQUESTS_СЕРВИС: Отправлен запрос к хранилищу на получение списка запросов");
        return getListGetItemRequestDtoFromListRequest(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId));
    }

    @Override
    public List<GetItemRequestDto> getAllItemRequestsFromIndexPageable(Long requesterId, Integer firstIndex, Integer size) {
        Sort sortByDataCreated = Sort.by("created").ascending();
        PageRequest pageRequest = PageRequest.of(firstIndex, size, sortByDataCreated);
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + requesterId));
        log.info("REQUESTS_СЕРВИС: Отправлен запрос к хранилищу на получение всех запросов постранично, {} запросов на странице", size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNot(requesterId, pageRequest);
        return getListGetItemRequestDtoFromListRequest(requests);
    }

    @Override
    public GetItemRequestDto getItemRequest(Long requesterId, Long requestId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + requesterId));
        log.info("REQUESTS_СЕРВИС: Отправлен запрос к хранилищу на получение запроса с Id {}", requestId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Нет запроса с ID: " + requestId));
        List<ItemForGetRequestDto> itemsDto = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemForGetRequestMapper::toItemForGetRequestDto)
                .collect(Collectors.toList());
        GetItemRequestDto girDto = GetItemRequestMapper.toGetItemRequestDto(request);
        setItemDtotListInGetItemRequestDto(girDto, itemsDto);

        return girDto;
    }

    private List<GetItemRequestDto> getListGetItemRequestDtoFromListRequest(List<ItemRequest> itemRequests) {
        List<Long> requestIdList = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemForGetRequestDto> itemsDto = itemRepository.findAllByRequestIdIn(requestIdList).stream()
                .map(ItemForGetRequestMapper::toItemForGetRequestDto)
                .collect(Collectors.toList());
        return itemRequests.stream()
                .map(GetItemRequestMapper::toGetItemRequestDto)
                .peek(gir -> setItemDtotListInGetItemRequestDto(gir, itemsDto))
                .collect(Collectors.toList());
    }

    private void setItemDtotListInGetItemRequestDto(GetItemRequestDto gir, List<ItemForGetRequestDto> itemsForGetRequestDto) {
        for (ItemForGetRequestDto ifgrDto : itemsForGetRequestDto)
            if (gir != null && ifgrDto.getRequestId().equals(gir.getId()))
                gir.getItems().add(ifgrDto);
    }
}
