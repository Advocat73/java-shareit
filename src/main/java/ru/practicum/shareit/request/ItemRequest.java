package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id; //уникальный идентификатор запроса
    @NotBlank(message = "Должно быть описание запроса")
    String description; //текст запроса, содержащий описание требуемой вещи
    @NotNull
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester; //пользователь, создавший запрос
    @NotNull
    @Column(name = "created_date")
    LocalDateTime created; //дата и время создания запроса
}
