package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull
    LocalDateTime startDate;
    @NotNull
    LocalDateTime endDate;
    @Enumerated(EnumType.STRING)
    BookingStatus status;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "booker_id")
    User booker;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;
}
