package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithDatesBookingDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private static final Long USER1_ID_FOR_TEST = 10L;
    private static final Long USER2_ID_FOR_TEST = 11L;
    private static final Long USER_NO_CORRECT_ID_FOR_TEST = 9L;
    private static final Long ITEM1_ID_FOR_TEST = 1L;
    private static final Long ITEM2_ID_FOR_TEST = 2L;
    private static final Long COMMENT1_ID = 21L;
    private static final Long COMMENT2_ID = 22L;
    private static final Long COMMENT3_ID = 23L;
    private static final Long COMMENT4_ID = 24L;
    private static final Long COMMENT5_ID = 25L;
    private static final Long COMMENT6_ID = 26L;
    private static final Long BOOKING1_ID = 101L;
    private static final Long BOOKING2_ID = 102L;
    private static final Long BOOKING3_ID = 103L;
    private static final Long BOOKING4_ID = 104L;

    private ItemService itemService;

    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    User user;
    ItemDto itemDto;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, itemRequestRepository, userRepository, bookingRepository, commentRepository);
        user = new User();
        user.setId(USER1_ID_FOR_TEST);
        user.setName("Vasya");
        user.setEmail("Vasya@mail.ru");
        itemDto = new ItemDto();
        itemDto.setName("Вещь");
        itemDto.setDescription("Очень нужная вещь");
        itemDto.setAvailable(true);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewItemNormal() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenAnswer((Answer<Item>) invocation -> {
            Item i = (Item) invocation.getArguments()[0];
            i.setId(ITEM1_ID_FOR_TEST);
            return i;
        });
        ItemDto itemDtoReceive = itemService.addNewItem(USER1_ID_FOR_TEST, itemDto);
        assertEquals(ITEM1_ID_FOR_TEST, itemDtoReceive.getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewItemNotSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        assertNull(itemService.addNewItem(USER1_ID_FOR_TEST, itemDto));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewItemThrowsNotFoundExceptionForNotExistUser() {
        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.addNewItem(USER1_ID_FOR_TEST, itemDto));
        assertEquals("Нет пользователя с ID: 10", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateItemNormal() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        Item baseItem = ItemMapper.fromItemDto(itemDto, user);
        baseItem.setId(ITEM1_ID_FOR_TEST);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(baseItem));
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenAnswer((Answer<Item>) invocation -> (Item) invocation.getArguments()[0]);

        ItemDto itemDtoUpd = new ItemDto();
        itemDtoUpd.setName("Штука");
        ItemDto itemDtoReceive = itemService.updateItem(USER1_ID_FOR_TEST, itemDtoUpd, ITEM1_ID_FOR_TEST);
        assertEquals(ITEM1_ID_FOR_TEST, itemDtoReceive.getId());
        assertEquals("Штука", itemDtoReceive.getName());
        assertEquals("Очень нужная вещь", itemDtoReceive.getDescription());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateItemOwnerNotValidThrowsBadRequestException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        Item baseItem = ItemMapper.fromItemDto(itemDto, user);
        baseItem.setId(ITEM1_ID_FOR_TEST);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(baseItem));
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenAnswer((Answer<Item>) invocation -> (Item) invocation.getArguments()[0]);

        ItemDto itemDtoUpd = new ItemDto();
        itemDtoUpd.setName("Штука");
        itemService.updateItem(USER1_ID_FOR_TEST, itemDtoUpd, ITEM1_ID_FOR_TEST);

        BadRequestException e = assertThrows(BadRequestException.class,
                () -> itemService.updateItem(USER_NO_CORRECT_ID_FOR_TEST, itemDtoUpd, ITEM1_ID_FOR_TEST));
        assertEquals("Пользователь с Id 9 не является собственником вещи с Id 1", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getUserItems() {
        List<Item> items = new ArrayList<>();
        Item item1 = ItemMapper.fromItemDto(itemDto, user);
        item1.setId(ITEM1_ID_FOR_TEST);
        items.add(item1);
        Item item2 = createSecondItem();
        items.add(item2);
        User commentator = new User();
        commentator.setId(USER2_ID_FOR_TEST);
        commentator.setName("Petya");
        commentator.setEmail("Petya@mail.ru");
        List<Coment> commentsItem1 = new ArrayList<>();
        List<Coment> commentsItem2 = new ArrayList<>();
        createTwoCommentsLists(commentsItem1, commentsItem2, commentator, item1, item2);
        List<Booking> listLastBooking = new ArrayList<>();
        List<Booking> listNextBooking = new ArrayList<>();
        createTwoListBookingLastAndNext(listLastBooking, listNextBooking, item1, commentator);

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(items);
        when(commentRepository.findAllByItemId(anyLong())).thenAnswer((Answer<List<Coment>>) invocation -> {
            Long itemId = (Long) invocation.getArguments()[0];
            if (itemId.equals(ITEM1_ID_FOR_TEST))
                return commentsItem1;
            if (itemId.equals(ITEM2_ID_FOR_TEST))
                return commentsItem2;
            return List.of();
        });
        when(bookingRepository.findAllByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(
                ArgumentMatchers.eq(ITEM1_ID_FOR_TEST), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(listLastBooking);
        when(bookingRepository.findAllByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(
                ArgumentMatchers.eq(ITEM2_ID_FOR_TEST), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(List.of());
        when(bookingRepository.findAllByItemIdAndStartDateAfterAndStatusNotOrderByStartDateAsc(
                ArgumentMatchers.eq(ITEM1_ID_FOR_TEST), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(listNextBooking);
        when(bookingRepository.findAllByItemIdAndStartDateAfterAndStatusNotOrderByStartDateAsc(
                ArgumentMatchers.eq(ITEM2_ID_FOR_TEST), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(List.of());

        List<ItemWithDatesBookingDto> itemsDto = itemService.getUserItems(USER1_ID_FOR_TEST, 0, 1);
        assertEquals(2, itemsDto.size());
        assertEquals(ITEM1_ID_FOR_TEST, itemsDto.get(0).getId());
        assertEquals(3, itemsDto.get(0).getComments().size());
        assertEquals(commentator.getId(), itemsDto.get(0).getLastBooking().getBooker().getId());
        assertNull(itemsDto.get(1).getLastBooking());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getItemNotExistItem() {
        when(itemRepository.findById(anyLong())).thenThrow(new NotFoundException("Нет вещи с Id: " + ITEM2_ID_FOR_TEST));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getItem(USER1_ID_FOR_TEST, ITEM2_ID_FOR_TEST));
        assertEquals("Нет вещи с Id: 2", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getItemNormalRequesterIsOwner() {
        Item item = ItemMapper.fromItemDto(itemDto, user);
        item.setId(ITEM1_ID_FOR_TEST);
        when(itemRepository.findById(ITEM1_ID_FOR_TEST)).thenReturn(Optional.of(item));

        List<Coment> commentsItem1 = new ArrayList<>();
        List<Coment> commentsItem2 = new ArrayList<>();
        createTwoCommentsLists(commentsItem1, commentsItem2, user, item, item);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(commentsItem2);

        User commentator = new User();
        commentator.setId(USER2_ID_FOR_TEST);
        commentator.setName("Petya");
        commentator.setEmail("Petya@mail.ru");
        List<Booking> listLastBooking = new ArrayList<>();
        List<Booking> listNextBooking = new ArrayList<>();
        createTwoListBookingLastAndNext(listLastBooking, listNextBooking, item, commentator);
        when(bookingRepository.findAllByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(
                ArgumentMatchers.eq(ITEM1_ID_FOR_TEST), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(listLastBooking);
        when(bookingRepository.findAllByItemIdAndStartDateAfterAndStatusNotOrderByStartDateAsc(
                ArgumentMatchers.eq(ITEM1_ID_FOR_TEST), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(listNextBooking);

        ItemWithDatesBookingDto iwdbDto = itemService.getItem(USER1_ID_FOR_TEST, ITEM1_ID_FOR_TEST);

        assertEquals(ITEM1_ID_FOR_TEST, iwdbDto.getId());
        assertEquals(3, iwdbDto.getComments().size());
        assertEquals(BOOKING2_ID, iwdbDto.getLastBooking().getId());
        assertEquals(BOOKING3_ID, iwdbDto.getNextBooking().getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getItemNormalRequesterIsNotOwner() {
        Item item = ItemMapper.fromItemDto(itemDto, user);
        item.setId(ITEM1_ID_FOR_TEST);
        when(itemRepository.findById(ITEM1_ID_FOR_TEST)).thenReturn(Optional.of(item));

        List<Coment> commentsItem1 = new ArrayList<>();
        List<Coment> commentsItem2 = new ArrayList<>();
        createTwoCommentsLists(commentsItem1, commentsItem2, user, item, item);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(commentsItem2);

        User commentator = new User();
        commentator.setId(USER2_ID_FOR_TEST);
        commentator.setName("Petya");
        commentator.setEmail("Petya@mail.ru");
        List<Booking> listLastBooking = new ArrayList<>();
        List<Booking> listNextBooking = new ArrayList<>();
        createTwoListBookingLastAndNext(listLastBooking, listNextBooking, item, commentator);
        verify(bookingRepository, never()).findAllByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(bookingRepository, never()).findAllByItemIdAndStartDateAfterAndStatusNotOrderByStartDateAsc(anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any());

        ItemWithDatesBookingDto iwdbDto = itemService.getItem(USER1_ID_FOR_TEST, ITEM1_ID_FOR_TEST);

        assertEquals(ITEM1_ID_FOR_TEST, iwdbDto.getId());
        assertEquals(3, iwdbDto.getComments().size());
        assertNull(iwdbDto.getLastBooking());
        assertNull(iwdbDto.getNextBooking());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void deleteItemNotOwner() {
        Item item = ItemMapper.fromItemDto(itemDto, user);
        item.setId(ITEM1_ID_FOR_TEST);
        when(itemRepository.findById(ITEM1_ID_FOR_TEST)).thenReturn(Optional.of(item));
        BadRequestException e = assertThrows(BadRequestException.class,
                () -> itemService.deleteItem(USER2_ID_FOR_TEST, ITEM1_ID_FOR_TEST));
        assertEquals("Пользователь с Id 11 не является собственником вещи с Id 1", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void deleteItemtOwner() {
        Item item = ItemMapper.fromItemDto(itemDto, user);
        item.setId(ITEM1_ID_FOR_TEST);
        when(itemRepository.findById(ITEM1_ID_FOR_TEST)).thenReturn(Optional.of(item));
        itemService.deleteItem(USER1_ID_FOR_TEST, ITEM1_ID_FOR_TEST);
    }

    @Test
    void addNewComment() {
        Item item = ItemMapper.fromItemDto(itemDto, user);
        item.setId(ITEM1_ID_FOR_TEST);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий для addNewComment");
        commentDto.setAuthorName("Petya");commentDto.setCreated(LocalDateTime.now());
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndDateBefore(any(), any(), any(), any()))
                .thenReturn(Optional.of(new Booking()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(ArgumentMatchers.any(Coment.class))).thenAnswer((Answer<Coment>) invocation -> {
            Coment comentArgument = (Coment) invocation.getArguments()[0];
            comentArgument.setId(COMMENT1_ID);
            return comentArgument;
        });
        CommentDto commentDtoReceive = itemService.addNewComment(USER2_ID_FOR_TEST, ITEM1_ID_FOR_TEST, commentDto);
        assertEquals(COMMENT1_ID, commentDtoReceive.getId());
    }

    private void createTwoCommentsLists(List<Coment> c1, List<Coment> c2, User commentator, Item firstItem, Item secondItem) {
        Coment com1 = new Coment();
        Coment com2 = new Coment();
        Coment com3 = new Coment();
        Coment com4 = new Coment();
        Coment com5 = new Coment();
        Coment com6 = new Coment();
        createComments(com1, com2, com3, com4, com5, com5, commentator, firstItem, secondItem);
        c1.add(com1);
        c1.add(com2);
        c1.add(com3);
        c2.add(com4);
        c2.add(com5);
        c2.add(com6);
    }

    private void createComments(Coment com1, Coment com2, Coment com3,
                                Coment com4, Coment com5, Coment com6,
                                User commentator, Item firstItem, Item secondItem) {
        com1.setId(COMMENT1_ID);
        com1.setAuthor(commentator);
        com1.setItem(firstItem);
        com1.setText("Comment_1 - Text");
        com1.setCreatedDate(LocalDateTime.of(2018, 1, 1, 12, 12));
        com2.setId(COMMENT2_ID);
        com2.setAuthor(commentator);
        com2.setItem(firstItem);
        com2.setText("Comment_2 - Text");
        com2.setCreatedDate(LocalDateTime.of(2019, 2, 1, 12, 12));
        com3.setId(COMMENT3_ID);
        com3.setAuthor(commentator);
        com3.setItem(firstItem);
        com3.setText("Comment_3 - Text");
        com3.setCreatedDate(LocalDateTime.of(2020, 3, 1, 12, 12));
        com4.setId(COMMENT4_ID);
        com4.setAuthor(commentator);
        com4.setItem(secondItem);
        com4.setText("Comment_4 - Text");
        com4.setCreatedDate(LocalDateTime.of(2021, 4, 1, 12, 12));
        com5.setId(COMMENT5_ID);
        com5.setAuthor(commentator);
        com5.setItem(secondItem);
        com5.setText("Comment_5 - Text");
        com5.setCreatedDate(LocalDateTime.of(2022, 5, 1, 12, 12));
        com6.setId(COMMENT6_ID);
        com6.setAuthor(commentator);
        com6.setItem(secondItem);
        com6.setText("Comment_6 - Text");
        com6.setCreatedDate(LocalDateTime.of(2023, 6, 1, 12, 12));
    }

    private Item createSecondItem() {
        Item item2 = new Item();
        item2.setName("Штуковина");
        item2.setDescription("Всем нужна такая");
        item2.setOwner(user);
        item2.setAvailable(true);
        item2.setId(ITEM2_ID_FOR_TEST);
        return item2;
    }

    private void createTwoListBookingLastAndNext(List<Booking> last, List<Booking> next, Item item, User booker) {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        Booking booking3 = new Booking();
        Booking booking4 = new Booking();
        createBookings(booking1, booking2, booking3, booking4, item, booker);
        last.add(booking2);
        last.add(booking1);
        next.add(booking3);
        next.add(booking4);
    }

    private void createBookings(Booking booking1, Booking booking2, Booking booking3, Booking booking4, Item item, User booker) {
        booking1.setId(BOOKING1_ID);
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStartDate(LocalDateTime.of(2023, 1, 1, 1, 1));
        booking1.setEndDate(LocalDateTime.of(2024, 1, 1, 1, 1));
        booking2.setId(BOOKING2_ID);
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStartDate(LocalDateTime.of(2023, 2, 2, 2, 2));
        booking2.setEndDate(LocalDateTime.of(2024, 2, 2, 2, 2));
        booking3.setId(BOOKING3_ID);
        booking3.setItem(item);
        booking3.setBooker(booker);
        booking3.setStatus(BookingStatus.APPROVED);
        booking3.setStartDate(LocalDateTime.of(2025, 3, 3, 3, 3));
        booking3.setEndDate(LocalDateTime.of(2026, 3, 3, 3, 3));
        booking4.setId(BOOKING4_ID);
        booking4.setItem(item);
        booking4.setBooker(booker);
        booking4.setStatus(BookingStatus.APPROVED);
        booking4.setStartDate(LocalDateTime.of(2025, 4, 4, 4, 4));
        booking4.setEndDate(LocalDateTime.of(2026, 4, 4, 4, 4));
    }
}