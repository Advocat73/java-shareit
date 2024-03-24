package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private final EasyRandom generator = new EasyRandom();
    private final UserMapper userMapper = new UserMapperImpl();

    private static final Long BOOKER_ID = 1L;
    private static final Long OWNER_ID = 2L;
    private static final Long NOT_OWNER_AND_BOOKER_ID = 3L;

    private static final Long ITEM_ID_1 = 1L;
    private static final Long ITEM_ID_2 = 2L;

    private static final Long BOOKING_ID = 1L;
    private static final Long BOOKING_REJECTED_PAST_ID = 2L;
    private static final Long BOOKING_CANCELED_CURRENT_ID = 3L;
    private static final Long BOOKING_WAITING_FUTURE_ID = 4L;
    private static final Long BOOKING_APPROVED_FUTURE_ID = 5L;

    private User owner;
    private User booker;
    private Item item1;
    private Item item2;

    private BookingDto bookingDto;
    private Booking booking;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;


    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        UserDto ownerDto = createUserDto(OWNER_ID);
        owner = userMapper.fromUserDto(ownerDto);
        UserDto bookerDto = createUserDto(BOOKER_ID);
        booker = userMapper.fromUserDto(bookerDto);
        ItemDto itemDto1 = createItemDto(ITEM_ID_1);
        item1 = ItemMapper.fromItemDto(itemDto1, owner);
        ItemDto itemDto2 = createItemDto(ITEM_ID_2);
        item2 = ItemMapper.fromItemDto(itemDto2, owner);
        bookingDto = createBookingDto(BOOKING_ID);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewBookingNoItem() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.addNewBooking(BOOKER_ID, bookingDto));
        assertEquals("Нет вещи с ID: " + ITEM_ID_1, e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewBookingNoBooker() {
        when(itemRepository.findById(ITEM_ID_1)).thenReturn(Optional.ofNullable(item1));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.addNewBooking(BOOKER_ID, bookingDto));
        assertEquals("Нет пользователя с ID: " + BOOKER_ID, e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewBookingBookerIsOwner() {
        item1.setOwner(booker);
        when(itemRepository.findById(ITEM_ID_1)).thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.addNewBooking(BOOKER_ID, bookingDto));
        assertEquals("Собственник не может бронировать свою вещь", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewBookingItemNotAvailable() {
        item1.setOwner(owner);
        when(itemRepository.findById(ITEM_ID_1)).thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        item1.setAvailable(false);
        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.addNewBooking(BOOKER_ID, bookingDto));
        assertEquals("Вещь с Id " + ITEM_ID_1 + " не доступна для бронирования", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewBookingWithNotValidDates() {
        item1.setOwner(owner);
        when(itemRepository.findById(ITEM_ID_1)).thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        setBookingDatesNotValid(bookingDto);
        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.addNewBooking(BOOKER_ID, bookingDto));
        assertEquals("При запросе на бронирование вещи с Id " + bookingDto.getItemId() + " даты бронирования указаны не валидно", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewBooking() {
        item1.setOwner(owner);
        when(itemRepository.findById(ITEM_ID_1)).thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.fromBookingDto(bookingDto, booker, item1, BookingStatus.WAITING));
        BookingDto bookingDtoReceived = bookingService.addNewBooking(BOOKER_ID, bookingDto);
        assertEquals(BOOKING_ID, bookingDtoReceived.getId());
        assertEquals(BOOKER_ID, bookingDtoReceived.getBookerId());
        assertEquals(ITEM_ID_1, bookingDtoReceived.getItemId());
        assertEquals(BookingStatus.WAITING.toString(), bookingDtoReceived.getStatus());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateBookingNotExist() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.updateBooking(BOOKER_ID, BOOKING_ID, true));
        assertEquals("Нет бронирования с ID: " + BOOKING_ID, e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateBookingStatusNotWaiting() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.ofNullable(BookingMapper.fromBookingDto(bookingDto, booker, item1, BookingStatus.APPROVED)));
        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.updateBooking(BOOKER_ID, BOOKING_ID, true));
        assertEquals("Бронирование не находится в состоянии ожидания одобрения", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateBookingStatusNotOwner() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.ofNullable(BookingMapper.fromBookingDto(bookingDto, booker, item1, BookingStatus.WAITING)));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.updateBooking(BOOKER_ID, BOOKING_ID, true));
        assertEquals("Подтвердить бронирование вещи может только собственник", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateBooking() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.ofNullable(BookingMapper.fromBookingDto(bookingDto, booker, item1, BookingStatus.WAITING)));
        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.fromBookingDto(bookingDto, booker, item1, BookingStatus.APPROVED));
        BookingDto bookingDtoReceived = bookingService.updateBooking(OWNER_ID, BOOKING_ID, true);
        assertEquals(BOOKING_ID, bookingDtoReceived.getId());
        assertEquals(BOOKER_ID, bookingDtoReceived.getBookerId());
        assertEquals(ITEM_ID_1, bookingDtoReceived.getItemId());
        assertEquals(BookingStatus.APPROVED.toString(), bookingDtoReceived.getStatus());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookingNoBooker() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(BOOKER_ID, BOOKING_ID));
        assertEquals("Нет пользователя с ID: " + BOOKER_ID, e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookingNoBooking() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(BOOKER_ID, BOOKING_ID));
        assertEquals("Нет бронирования с ID: " + BOOKING_ID, e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookingNotOwnerNotBooker() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(BookingMapper.fromBookingDto(bookingDto, booker, item1, BookingStatus.APPROVED)));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(NOT_OWNER_AND_BOOKER_ID, BOOKING_ID));
        assertEquals("Запрос на получения бронирования может сделать только собственник вещи или бронирователь", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.ofNullable(BookingMapper.fromBookingDto(bookingDto, booker, item1, BookingStatus.APPROVED)));
        BookingDto bookingDtoReceived = bookingService.getBooking(BOOKER_ID, BOOKING_ID);
        assertEquals(BOOKING_ID, bookingDtoReceived.getId());
        assertEquals(BOOKER_ID, bookingDtoReceived.getBookerId());
        assertEquals(ITEM_ID_1, bookingDtoReceived.getItemId());
        assertEquals(BookingStatus.APPROVED.toString(), bookingDtoReceived.getStatus());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookerBookingsNoBooker() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookerBookings(BOOKER_ID, "ALL", 0, 10));
        assertEquals("Нет пользователя с ID: " + BOOKER_ID, e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookerBookingsStateAll() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        prepareBookingsForTestingState();
        // ALL
        when(bookingRepository.findAllByBookerId(any(), any())).thenReturn(List.of(booking, booking2, booking3, booking4, booking5));
        List<BookingDto> bookingDtosReceived = bookingService.getBookerBookings(BOOKER_ID, "ALL", 0, 10);
        assertEquals(5, bookingDtosReceived.size());
        assertEquals(BOOKING_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookerBookingsStateCurrent() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        prepareBookingsForTestingState();
        // CURRENT
        when(bookingRepository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByIdAsc(any(), any(), any(), any()))
                .thenReturn(List.of(booking3));
        List<BookingDto> bookingDtosReceived = bookingService.getBookerBookings(BOOKER_ID, "CURRENT", 0, 10);
        assertEquals(1, bookingDtosReceived.size());
        assertEquals(BOOKING_CANCELED_CURRENT_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookerBookingsStatePast() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        prepareBookingsForTestingState();
        // PAST
        when(bookingRepository.findAllByBookerIdAndEndDateBefore(any(), any(), any()))
                .thenReturn(List.of(booking2));
        List<BookingDto> bookingDtosReceived = bookingService.getBookerBookings(BOOKER_ID, "PAST", 0, 10);
        assertEquals(1, bookingDtosReceived.size());
        assertEquals(BOOKING_REJECTED_PAST_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookerBookingsStateFuture() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        prepareBookingsForTestingState();
        // FUTURE
        when(bookingRepository.findAllByBookerIdAndStartDateAfter(any(), any(), any()))
                .thenReturn(List.of(booking, booking4, booking5));
        List<BookingDto> bookingDtosReceived = bookingService.getBookerBookings(BOOKER_ID, "FUTURE", 0, 10);
        assertEquals(3, bookingDtosReceived.size());
        assertEquals(BOOKING_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookerBookingsStateWaiting() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        prepareBookingsForTestingState();
        // WAITING
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking4));
        List<BookingDto> bookingDtosReceived = bookingService.getBookerBookings(BOOKER_ID, "WAITING", 0, 10);
        assertEquals(1, bookingDtosReceived.size());
        assertEquals(BOOKING_WAITING_FUTURE_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookerBookingsStateRejected() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        prepareBookingsForTestingState();
        // REJECTED
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking2));
        List<BookingDto> bookingDtosReceived = bookingService.getBookerBookings(BOOKER_ID, "REJECTED", 0, 10);
        assertEquals(1, bookingDtosReceived.size());
        assertEquals(BOOKING_REJECTED_PAST_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getBookerBookingsStateUnsupported() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(booker));
        prepareBookingsForTestingState();
        // UNSUPPORTED
        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.getBookerBookings(BOOKER_ID, "UNSUPPORTED", 0, 10));
        assertEquals("Unknown state: UNSUPPORTED", e.getMessage());
    }


    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsNoOwner() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookerBookings(OWNER_ID, "ALL", 0, 10));
        assertEquals("Нет пользователя с ID: " + OWNER_ID, e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsNoItems() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(OWNER_ID)).thenReturn(List.of());
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerItemBookings(OWNER_ID, "ALL", 0, 10));
        assertEquals("У пользователя с ID: " + OWNER_ID + " нет вещей для бронирования", e.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsStateAll() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(OWNER_ID)).thenReturn(List.of(item1, item2));
        prepareBookingsForTestingState();
        // ALL
        when(bookingRepository.findAllByItemId(eq(ITEM_ID_1), any())).thenReturn(List.of(booking3, booking, booking5, booking4, booking2));
        when(bookingRepository.findAllByItemId(eq(ITEM_ID_2), any())).thenReturn(List.of());
        List<BookingDto> bookingDtosReceived = bookingService.getOwnerItemBookings(OWNER_ID, "ALL", 0, 10);
        assertEquals(5, bookingDtosReceived.size());
        assertEquals(BOOKING_APPROVED_FUTURE_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsStateCurrent() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(OWNER_ID)).thenReturn(List.of(item1, item2));
        prepareBookingsForTestingState();
        // CURRENT
        when(bookingRepository.findAllByItemIdAndStartDateBeforeAndEndDateAfter(eq(ITEM_ID_1), any(), any(), any())).thenReturn(List.of(booking3));
        when(bookingRepository.findAllByItemIdAndStartDateBeforeAndEndDateAfter(eq(ITEM_ID_2), any(), any(), any())).thenReturn(List.of());
        List<BookingDto> bookingDtosReceived = bookingService.getOwnerItemBookings(OWNER_ID, "CURRENT", 0, 10);
        assertEquals(1, bookingDtosReceived.size());
        assertEquals(BOOKING_CANCELED_CURRENT_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsStatePast() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(OWNER_ID)).thenReturn(List.of(item1, item2));
        prepareBookingsForTestingState();
        // PAST
        when(bookingRepository.findAllByItemIdAndEndDateBefore(eq(ITEM_ID_1), any(), any())).thenReturn(List.of(booking2));
        when(bookingRepository.findAllByItemIdAndEndDateBefore(eq(ITEM_ID_2), any(), any())).thenReturn(List.of());
        List<BookingDto> bookingDtosReceived = bookingService.getOwnerItemBookings(OWNER_ID, "PAST", 0, 10);
        assertEquals(1, bookingDtosReceived.size());
        assertEquals(BOOKING_REJECTED_PAST_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsStateFuture() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(OWNER_ID)).thenReturn(List.of(item1, item2));
        prepareBookingsForTestingState();
        // FUTURE
        when(bookingRepository.findAllByItemIdAndStartDateAfter(eq(ITEM_ID_1), any(), any())).thenReturn(List.of(booking4, booking5, booking));
        when(bookingRepository.findAllByItemIdAndStartDateAfter(eq(ITEM_ID_2), any(), any())).thenReturn(List.of());
        List<BookingDto> bookingDtosReceived = bookingService.getOwnerItemBookings(OWNER_ID, "FUTURE", 0, 10);
        assertEquals(3, bookingDtosReceived.size());
        assertEquals(BOOKING_APPROVED_FUTURE_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsStateWaiting() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(OWNER_ID)).thenReturn(List.of(item1, item2));
        prepareBookingsForTestingState();
        // WAITING
        when(bookingRepository.findAllByItemIdAndStatus(eq(ITEM_ID_1), any(), any())).thenReturn(List.of(booking4));
        when(bookingRepository.findAllByItemIdAndStatus(eq(ITEM_ID_2), any(), any())).thenReturn(List.of());
        List<BookingDto> bookingDtosReceived = bookingService.getOwnerItemBookings(OWNER_ID, "WAITING", 0, 10);
        assertEquals(1, bookingDtosReceived.size());
        assertEquals(BOOKING_WAITING_FUTURE_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsStateRejected() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(OWNER_ID)).thenReturn(List.of(item1, item2));
        prepareBookingsForTestingState();
        // REJECTED
        when(bookingRepository.findAllByItemIdAndStatus(eq(ITEM_ID_1), any(), any())).thenReturn(List.of(booking2));
        when(bookingRepository.findAllByItemIdAndStatus(eq(ITEM_ID_2), any(), any())).thenReturn(List.of());
        List<BookingDto> bookingDtosReceived = bookingService.getOwnerItemBookings(OWNER_ID, "REJECTED", 0, 10);
        assertEquals(1, bookingDtosReceived.size());
        assertEquals(BOOKING_REJECTED_PAST_ID, bookingDtosReceived.get(0).getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getOwnerItemBookingsStateUnsupported() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(OWNER_ID)).thenReturn(List.of(item1, item2));
        prepareBookingsForTestingState();
        // UNSUPPORTED
        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.getOwnerItemBookings(OWNER_ID, "UNSUPPORTED", 0, 10));
        assertEquals("Unknown state: UNSUPPORTED", e.getMessage());
    }

    private BookingDto createBookingDto(Long bookingId) {
        BookingDto bookingDto = generator.nextObject(BookingDto.class);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            LocalDateTime tmp = bookingDto.getStart();
            bookingDto.setStart(bookingDto.getEnd());
            bookingDto.setEnd(tmp);
        }
        LocalDateTime start = LocalDateTime.of(2025, bookingDto.getStart().getMonth(), bookingDto.getStart().getDayOfMonth(), bookingDto.getStart().getHour(), bookingDto.getStart().getMinute(), bookingDto.getStart().getSecond());
        LocalDateTime end = LocalDateTime.of(2026, bookingDto.getEnd().getMonth(), bookingDto.getEnd().getDayOfMonth(), bookingDto.getEnd().getHour(), bookingDto.getEnd().getMinute(), bookingDto.getEnd().getSecond());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setId(bookingId);
        bookingDto.setItemId(ITEM_ID_1);
        bookingDto.setBookerId(BOOKER_ID);
        if (bookingDto.getBookerId() < 0) {
            long l = bookingDto.getBookerId();
            l *= (-1);
            bookingDto.setBookerId(l);
        }
        if (bookingDto.getItemId() < 0) {
            long l = bookingDto.getItemId();
            l *= (-1);
            bookingDto.setItemId(l);
        }
        bookingDto.setStatus("ALL");
        return bookingDto;
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

    private void setBookingDatesNotValid(BookingDto bookingDto) {
        LocalDateTime start = LocalDateTime.of(2025, bookingDto.getStart().getMonth(), bookingDto.getStart().getDayOfMonth(), bookingDto.getStart().getHour(), bookingDto.getStart().getMinute(), bookingDto.getStart().getSecond());
        LocalDateTime end = LocalDateTime.of(2023, bookingDto.getEnd().getMonth(), bookingDto.getEnd().getDayOfMonth(), bookingDto.getEnd().getHour(), bookingDto.getEnd().getMinute(), bookingDto.getEnd().getSecond());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
    }

    private void setBookingDatesPast(BookingDto bookingDto) {
        LocalDateTime start = LocalDateTime.of(2022, bookingDto.getStart().getMonth(), bookingDto.getStart().getDayOfMonth(), bookingDto.getStart().getHour(), bookingDto.getStart().getMinute(), bookingDto.getStart().getSecond());
        LocalDateTime end = LocalDateTime.of(2023, bookingDto.getEnd().getMonth(), bookingDto.getEnd().getDayOfMonth(), bookingDto.getEnd().getHour(), bookingDto.getEnd().getMinute(), bookingDto.getEnd().getSecond());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
    }

    private void setBookingDatesCurrent(BookingDto bookingDto) {
        LocalDateTime start = LocalDateTime.of(2023, bookingDto.getStart().getMonth(), bookingDto.getStart().getDayOfMonth(), bookingDto.getStart().getHour(), bookingDto.getStart().getMinute(), bookingDto.getStart().getSecond());
        LocalDateTime end = LocalDateTime.of(2025, bookingDto.getEnd().getMonth(), bookingDto.getEnd().getDayOfMonth(), bookingDto.getEnd().getHour(), bookingDto.getEnd().getMinute(), bookingDto.getEnd().getSecond());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
    }

    private void prepareBookingsForTestingState() {
        BookingDto bookingDto2 = createBookingDto(BOOKING_REJECTED_PAST_ID);
        BookingDto bookingDto3 = createBookingDto(BOOKING_CANCELED_CURRENT_ID);
        BookingDto bookingDto4 = createBookingDto(BOOKING_WAITING_FUTURE_ID);
        BookingDto bookingDto5 = createBookingDto(BOOKING_APPROVED_FUTURE_ID);
        setBookingDatesCurrent(bookingDto);
        booking = BookingMapper.fromBookingDto(bookingDto, booker, item1, BookingStatus.APPROVED);
        setBookingDatesPast(bookingDto2);
        booking2 = BookingMapper.fromBookingDto(bookingDto2, booker, item1, BookingStatus.REJECTED);
        setBookingDatesCurrent(bookingDto3);
        booking3 = BookingMapper.fromBookingDto(bookingDto3, booker, item1, BookingStatus.CANCELED);
        // Set booking date in future
        booking4 = BookingMapper.fromBookingDto(bookingDto4, booker, item1, BookingStatus.WAITING);
        booking5 = BookingMapper.fromBookingDto(bookingDto5, booker, item1, BookingStatus.APPROVED);
    }
}