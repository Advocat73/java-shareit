package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    Long id;
    /**
     текст запроса, содержащий описание требуемой вещи
     */
    @NotBlank(message = "Должно быть описание запроса")
    private String description;
    /**
     дата и время создания запроса
     */
    private LocalDateTime created = LocalDateTime.now();
}
