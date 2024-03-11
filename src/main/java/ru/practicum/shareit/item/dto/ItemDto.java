package ru.practicum.shareit.item.dto;

import jdk.jfr.BooleanFlag;
import lombok.Data;
import ru.practicum.shareit.item.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;
    @NotBlank(message = "Должно быть описание вещи")
    private String description;
    @NotNull
    @BooleanFlag
    private Boolean available;
    private List<CommentDto> comments = new ArrayList<>();
    private Long requestId;
}
