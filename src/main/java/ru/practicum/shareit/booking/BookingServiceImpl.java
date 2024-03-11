package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto addNewBooking(Long bookerId, BookingDto bookingDto) {
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Нет вещи с ID: " + itemId));
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + bookerId));
        if (bookerId.equals(item.getOwner().getId()))
            throw new NotFoundException("Собственник не может бронировать свою вещь");
        if (!item.getAvailable())
            throw new BadRequestException("Вещь с Id " + itemId + " не доступна для бронирования");

        Booking booking = BookingMapper.fromBookingDto(bookingDto, booker, item, BookingStatus.WAITING);

        log.info("BOOKING_СЕРВИС: Отправлен запрос к хранилищу на добавленние бронирования вещи c ID {} пользователем с ID {}", itemId, bookerId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long requesterId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Нет бронирования с ID: " + bookingId));
        if (booking.getStatus() != BookingStatus.WAITING)
            throw new BadRequestException("Бронирование не находится в состоянии ожидания одобрения");
        if (!Objects.equals(booking.getItem().getOwner().getId(), requesterId))
            throw new NotFoundException("Подтвердить бронирование вещи может только собственник");

        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        log.info("BOOKING_СЕРВИС: Отправлен запрос к хранилищу от на обновление статуса бронирования вещи c ID {} c WAITING на {}", booking.getItem().getId(), booking.getStatus());
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long requesterId, Long bookingId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + requesterId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Нет бронирования с ID: " + bookingId));
        if (!Objects.equals(requesterId, booking.getItem().getOwner().getId()) && !Objects.equals(requesterId, booking.getBooker().getId()))
            throw new NotFoundException("Запрос на получения бронирования может сделать только собственник вещи или бронирователь");
        log.info("BOOKING_СЕРВИС: Отправлен запрос к хранилищу от на получения бронирования вещи c ID {}", booking.getItem().getId());
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookerBookings(Long requesterId, String state) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + requesterId));
        return getUserBookingsByState(requesterId, state).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerItemBookings(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + ownerId));
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId);
        if (items.size() == 0)
            throw new NotFoundException("У пользователя с ID: " + ownerId + " нет вещей для бронирования");
        return items.stream()
                .map(item -> getOwnerItemsBookingsByState(item.getId(), state))
                .flatMap(Collection::stream)
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private List<Booking> getUserBookingsByState(Long userId, String state) {
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByIdDesc(userId);
            case "CURRENT":
                LocalDateTime localDateTime = LocalDateTime.now();
                return bookingRepository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByIdAsc(userId, localDateTime, localDateTime);
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByIdDesc(userId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartDateAfterOrderByIdDesc(userId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByIdDesc(userId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByIdDesc(userId, BookingStatus.REJECTED);
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
    }

    private List<Booking> getOwnerItemsBookingsByState(Long itemId, String state) {
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemIdOrderByIdDesc(itemId);
            case "CURRENT":
                LocalDateTime localDateTime = LocalDateTime.now();
                return bookingRepository.findAllByItemIdAndStartDateBeforeAndEndDateAfterOrderByIdDesc(itemId, localDateTime, localDateTime);
            case "PAST":
                return bookingRepository.findAllByItemIdAndEndDateBeforeOrderByIdDesc(itemId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByItemIdAndStartDateAfterOrderByIdDesc(itemId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByItemIdAndStatusOrderByIdDesc(itemId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByItemIdAndStatusOrderByIdDesc(itemId, BookingStatus.REJECTED);
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
    }
}
