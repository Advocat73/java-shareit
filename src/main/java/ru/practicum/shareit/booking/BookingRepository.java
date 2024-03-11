package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    public Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndDateBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime currentTime);

    public List<Booking> findAllByItemIdOrderByIdDesc(Long itemId);

    public List<Booking> findAllByItemIdAndStatusOrderByIdDesc(Long itemId, BookingStatus status);

    public List<Booking> findAllByItemIdAndEndDateBeforeOrderByIdDesc(Long itemId, LocalDateTime currentTime);

    public List<Booking> findAllByItemIdAndStartDateAfterOrderByIdDesc(Long itemId, LocalDateTime currentTime);

    public List<Booking> findAllByItemIdAndStartDateBeforeAndEndDateAfterOrderByIdDesc(Long itemId, LocalDateTime curTime, LocalDateTime currentTime);

    public List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByIdAsc(Long requesterId, LocalDateTime curTime, LocalDateTime currentTime);

    public List<Booking> findAllByBookerIdOrderByIdDesc(Long requesterId);

    public List<Booking> findAllByBookerIdAndStatusOrderByIdDesc(Long requesterId, BookingStatus status);

    public List<Booking> findAllByBookerIdAndEndDateBeforeOrderByIdDesc(Long requesterId, LocalDateTime currentTime);

    public List<Booking> findAllByBookerIdAndStartDateAfterOrderByIdDesc(Long requesterId, LocalDateTime currentTime);

    public List<Booking> findAllByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    public List<Booking> findAllByItemIdAndStartDateAfterAndStatusNotOrderByStartDateAsc(Long itemId, LocalDateTime currentTime, BookingStatus status);
}
