package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private final EasyRandom generator = new EasyRandom();
    private final UserMapper userMapper = new UserMapperImpl();

    private static final Long REQUESTER_ID = 1L;

    private static final Long ITEM_REQUEST_ID = 1L;
    private static final Long ITEM_REQUEST2_ID = 2L;
    private static final Long ITEM_REQUEST3_ID = 3L;

    private static final Long ITEM_ID_1 = 1L;
    private static final Long ITEM_ID_2 = 2L;
    private static final Long ITEM_ID_3 = 3L;


    private UserDto requesterDto;
    private User requester;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest2;
    private ItemRequestDto itemRequestDto2;
    private ItemRequest itemRequest3;
    private ItemRequestDto itemRequestDto3;

    ItemDto itemDto1;
    ItemDto itemDto2;
    ItemDto itemDto3;
    Item item1;
    Item item2;
    Item item3;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository);
        requesterDto = createUserDto(REQUESTER_ID);
        requester = userMapper.fromUserDto(requesterDto);

        itemRequestDto = createItemRequestDto(ITEM_REQUEST_ID);
        setYearInItemRequestCreated(itemRequestDto, 2021);
        itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, requester);
        itemRequestDto2 = createItemRequestDto(ITEM_REQUEST2_ID);
        setYearInItemRequestCreated(itemRequestDto2, 2023);
        itemRequest2 = ItemRequestMapper.fromItemRequestDto(itemRequestDto2, requester);
        itemRequestDto3 = createItemRequestDto(ITEM_REQUEST3_ID);
        setYearInItemRequestCreated(itemRequestDto3, 2022);
        itemRequest3 = ItemRequestMapper.fromItemRequestDto(itemRequestDto3, requester);

        ItemDto itemDto1 = createItemDto(ITEM_ID_1);
        item1 = ItemMapper.fromItemDto(itemDto1, requester);
        ItemDto itemDto2 = createItemDto(ITEM_ID_2);
        item2 = ItemMapper.fromItemDto(itemDto2, requester);
        ItemDto itemDto3 = createItemDto(ITEM_ID_3);
        item3 = ItemMapper.fromItemDto(itemDto3, requester);
    }

    @Test
    void addNewRequestItemNoRequester() {
        when(userRepository.findById(REQUESTER_ID)).thenThrow(new NotFoundException("Нет пользователя с ID: " + REQUESTER_ID));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.addNewRequestItem(REQUESTER_ID, itemRequestDto));
        assertEquals("Нет пользователя с ID: " + REQUESTER_ID, e.getMessage());
    }

    @Test
    void addNewRequestItem() {
        when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDto itemRequestDtoReceived = itemRequestService.addNewRequestItem(REQUESTER_ID, itemRequestDto);
        assertEquals(REQUESTER_ID, itemRequestDtoReceived.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequestDtoReceived.getDescription());
    }

    @Test
    void getRequesterItemsNotRequestor() {
        when(userRepository.findById(REQUESTER_ID)).thenThrow(new NotFoundException("Нет пользователя с ID: " + REQUESTER_ID));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequesterItems(REQUESTER_ID));
        assertEquals("Нет пользователя с ID: " + REQUESTER_ID, e.getMessage());
    }

    @Test
    void getRequesterItems() {
        when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.ofNullable(requester));
        addRequestInfoToItem(itemRequest, item1);
        addRequestInfoToItem(itemRequest2, item1);
        addRequestInfoToItem(itemRequest2, item2);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(REQUESTER_ID)).thenReturn(Stream.of(itemRequest, itemRequest2, itemRequest3)
                .sorted((ir1, ir2) -> (ir1.getCreated().isAfter(ir2.getCreated())) ? -1 : 1).collect(Collectors.toList()));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of(item1, item2));
        List<GetItemRequestDto> irs = itemRequestService.getRequesterItems(REQUESTER_ID);
        assertEquals(3, irs.size());
        assertEquals(ITEM_REQUEST2_ID, irs.get(0).getId());
        assertEquals(2, irs.get(0).getItems().size());
        assertEquals(0, irs.get(2).getItems().size());
    }

    @Test
    void getAllItemRequestsFromIndexPageable() {
    }

    @Test
    void getItemRequestNoRequester() {
        when(userRepository.findById(REQUESTER_ID)).thenThrow(new NotFoundException("Нет пользователя с ID: " + REQUESTER_ID));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(REQUESTER_ID, ITEM_REQUEST_ID));
        assertEquals("Нет пользователя с ID: " + REQUESTER_ID, e.getMessage());
    }

    @Test
    void getItemRequestNoItemRequest() {
        when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.ofNullable(requester));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(REQUESTER_ID, ITEM_REQUEST_ID));
        assertEquals("Нет запроса с ID: " + ITEM_REQUEST_ID, e.getMessage());
        //itemRequestService.getItemRequest(REQUESTER_ID, ITEM_REQUEST_ID);
    }

    @Test
    void getItemRequest() {
        addRequestInfoToItem(itemRequest, item1);
        addRequestInfoToItem(itemRequest, item2);
        when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findById(ITEM_REQUEST_ID)).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findAllByRequestId(ITEM_REQUEST_ID)).thenReturn(List.of(item1, item2));
        GetItemRequestDto girDto = itemRequestService.getItemRequest(REQUESTER_ID, ITEM_REQUEST_ID);
        assertEquals(2, girDto.getItems().size());
        assertEquals(ITEM_REQUEST_ID, girDto.getId());
        assertEquals(itemRequest.getDescription(), girDto.getDescription());
    }

    private UserDto createUserDto(Long userId) {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setId(userId);
        String email = userDto.getEmail() + "@mail.ru";
        userDto.setEmail(email);
        return userDto;
    }

    private ItemDto createItemDto(Long itemId) {
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        itemDto.setId(itemId);
        itemDto.setAvailable(true);
        return itemDto;
    }

    private ItemRequestDto createItemRequestDto(Long itemRequestId) {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        itemRequestDto.setId(itemRequestId);
        return itemRequestDto;
    }

    private void addRequestInfoToItem(ItemRequest itemRequest, Item item) {
        item.setRequest(itemRequest);
    }

    private void setYearInItemRequestCreated(ItemRequestDto itemRequestDto, int year) {
        LocalDateTime created = LocalDateTime.of(year, itemRequestDto.getCreated().getMonth(), itemRequestDto.getCreated().getDayOfMonth(),
                itemRequestDto.getCreated().getHour(), itemRequestDto.getCreated().getMinute(), itemRequestDto.getCreated().getSecond());
        itemRequestDto.setCreated(created);
    }
}