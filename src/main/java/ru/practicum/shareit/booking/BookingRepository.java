package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    public Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndDateBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime currentTime);

    public List<Booking> findAllByItemIdOrderByIdDesc(Long itemId, PageRequest pageRequest);

    public List<Booking> findAllByItemIdAndStatusOrderByIdDesc(Long itemId, BookingStatus status, PageRequest pageRequest);

    public List<Booking> findAllByItemIdAndEndDateBeforeOrderByIdDesc(Long itemId, LocalDateTime currentTime, PageRequest pageRequest);

    public List<Booking> findAllByItemIdAndStartDateAfterOrderByIdDesc(Long itemId, LocalDateTime currentTime, PageRequest pageRequest);

    public List<Booking> findAllByItemIdAndStartDateBeforeAndEndDateAfterOrderByIdDesc(Long itemId, LocalDateTime curTime, LocalDateTime currentTime, PageRequest pageRequest);

    public List<Booking> findAllByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    public List<Booking> findAllByItemIdAndStartDateAfterAndStatusNotOrderByStartDateAsc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    public List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByIdAsc(Long requesterId, LocalDateTime curTime, LocalDateTime currentTime, PageRequest pageRequest);

    public List<Booking> findAllByBookerId(Long requesterId, PageRequest pageRequest);

    public List<Booking> findAllByBookerIdAndStatus(Long requesterId, BookingStatus status, PageRequest pageRequest);

    public List<Booking> findAllByBookerIdAndEndDateBefore(Long requesterId, LocalDateTime currentTime, PageRequest pageRequest);

    public List<Booking> findAllByBookerIdAndStartDateAfter(Long requesterId, LocalDateTime currentTime, PageRequest pageRequest);
}
