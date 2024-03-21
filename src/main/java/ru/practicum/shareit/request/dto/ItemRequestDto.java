package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
//@AllArgsConstructor
public class ItemRequestDto {
    Long id;
    @NotBlank(message = "Должно быть описание запроса")
    private String description; //текст запроса, содержащий описание требуемой вещи
    private LocalDateTime created; //дата и время создания запроса
}
