package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndDateBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime currentTime);

    List<Booking> findAllByItemId(Long itemId, PageRequest pageRequest);

    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByItemIdAndEndDateBefore(Long itemId, LocalDateTime currentTime, PageRequest pageRequest);

    List<Booking> findAllByItemIdAndStartDateAfter(Long itemId, LocalDateTime currentTime, PageRequest pageRequest);

    List<Booking> findAllByItemIdAndStartDateBeforeAndEndDateAfter(Long itemId, LocalDateTime curTime, LocalDateTime currentTime, PageRequest pageRequest);

    List<Booking> findAllByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    List<Booking> findAllByItemIdAndStartDateAfterAndStatusNotOrderByStartDateAsc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByIdAsc(Long requesterId, LocalDateTime curTime, LocalDateTime currentTime, PageRequest pageRequest);

    List<Booking> findAllByBookerId(Long requesterId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatus(Long requesterId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndDateBefore(Long requesterId, LocalDateTime currentTime, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartDateAfter(Long requesterId, LocalDateTime currentTime, PageRequest pageRequest);
}
