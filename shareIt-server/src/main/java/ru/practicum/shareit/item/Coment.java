package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Coment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String text;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item = new Item();
    @ManyToOne
    @JoinColumn(name = "author_id")
    User author = new User();
    LocalDateTime createdDate = LocalDateTime.now();
}
