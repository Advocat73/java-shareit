package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + userId));
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на добавление новой вещи", userId);
        Item item = ItemMapper.fromItemDto(itemDto, owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Нет запроса с ID: " + itemDto.getRequestId()));
            item.setRequest(request);
        }
        return addItem(item);
    }

    @Override
    public ItemDto updateItem(Long ownerId, ItemDto itemDto, Long itemId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + ownerId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет вещи с Id " + itemId));
        if (!ownerId.equals(item.getOwner().getId()))
            throw new BadRequestException("Пользователь с Id " + ownerId + " не является собственником вещи с Id " + itemId);
        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на изменение вещи с id {}", ownerId, itemId);
        return addItem(item);
    }


    @Override
    public List<ItemWithDatesBookingDto> getUserItems(Long ownerId, Integer from, Integer size) {
        Sort sortByDataCreated = Sort.by("Huy").ascending();
        PageRequest pageRequest;
        try {
            pageRequest = PageRequest.of(from, size, sortByDataCreated);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Некорректные аргументы на постраничный вывод списка запросов: from: " + from + ", size: " + size);
        }
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу на получение вещей пользователя с id {}", ownerId);
        return itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(this::setLastAndNextBooking)
                .peek(iwdbDto -> iwdbDto.setComments(CommentMapper.toCommentDto(commentRepository.findAllByItemId(iwdbDto.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public ItemWithDatesBookingDto getItem(Long requesterId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет вещи с Id: " + itemId));
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на получение вещи с id {}", requesterId, itemId);
        ItemWithDatesBookingDto iwdbDto = (requesterId.equals(item.getOwner().getId())) ? setLastAndNextBooking(item)
                : ItemWithDatesBookingMapper.toItemWithDatesBookingDto(item);
        iwdbDto.setComments(CommentMapper.toCommentDto(commentRepository.findAllByItemId(itemId)));
        return iwdbDto;
    }

    @Override
    public List<ItemDto> searchItemBySubstring(String subStr) {
        if (subStr.isEmpty())
            return List.of();
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу на поиск вещи, содержащей текст {}", subStr);
        return itemRepository.searchItemBySustring(subStr).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет вещи с Id: " + itemId));
        if (!userId.equals(item.getOwner().getId()))
            throw new BadRequestException("Пользователь с Id " + userId + " не является собственником вещи с Id " + itemId);
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на удаление вещи с id {}", userId, itemId);
        itemRepository.delete(item);
    }

    @Override
    public CommentDto addNewComment(Long commentatorId, Long itemId, CommentDto commentDto) {
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndDateBefore(itemId, commentatorId, BookingStatus.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Нет законченного бронирования вещи с Id: " + itemId + " пользователем с Id " + commentatorId));
        User commentator = userRepository.findById(commentatorId).orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + commentatorId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет вещи с Id: " + itemId));
        log.info("ITEM_СЕРВИС: Отправлен запрос к хранилищу от пользователя с id {} на добавление комментария вещи с id {}", commentatorId, itemId);
        Coment comment = CommentMapper.fromCommentDto(commentDto, commentator, item);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemWithDatesBookingDto setLastAndNextBooking(Item item) {
        ItemWithDatesBookingDto iwdbDto = ItemWithDatesBookingMapper.toItemWithDatesBookingDto(item);
        LocalDateTime currentDate = LocalDateTime.now();
        List<Booking> listLastBooking = bookingRepository.findAllByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(item.getId(), currentDate, BookingStatus.REJECTED);
        Booking lastBooking = (listLastBooking.size() == 0) ? null : listLastBooking.get(0);
        List<Booking> listNextBooking = bookingRepository.findAllByItemIdAndStartDateAfterAndStatusNotOrderByStartDateAsc(item.getId(), currentDate, BookingStatus.REJECTED);
        Booking nextBooking = (listNextBooking.size() == 0) ? null : listNextBooking.get(0);
        iwdbDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        iwdbDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        return iwdbDto;
    }

    private ItemDto addItem(Item item) {
        try {
            return ItemMapper.toItemDto(itemRepository.save(item));
        } catch (RuntimeException e) {
            throw new DataConflictException("Вещь с названием " + item.getName() + " не добавлена в БД из-за неверных данных");
        }
    }
}

