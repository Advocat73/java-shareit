package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    Long id;
    @NotBlank(message = "Должно быть описание запроса")
    private String description; //текст запроса, содержащий описание требуемой вещи
    private LocalDateTime created = LocalDateTime.now(); //дата и время создания запроса
}
