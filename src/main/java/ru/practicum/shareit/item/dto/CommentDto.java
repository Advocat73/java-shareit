package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст комметария не может быть пустым")
    @Size(max = 500, message = "Длина текста комментария должна быть не больше {max} символов")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
