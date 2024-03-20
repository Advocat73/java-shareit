package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Coment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank(message = "Текст комметария не может быть пустым")
    @Size(max = 500, message = "Длина текста комментария должна быть не больше {max} символов")
    String text;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item = new Item();
    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    User author = new User();
    @NotNull
    LocalDateTime createdDate = LocalDateTime.now();
}
