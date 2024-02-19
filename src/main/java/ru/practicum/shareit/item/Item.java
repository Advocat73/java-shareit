package ru.practicum.shareit.item;

import jdk.jfr.BooleanFlag;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {
    Long id;
    @NotBlank(message = "Название вещи не может быть пустым")
    String name;
    @NotBlank(message = "Должно быть описание вещи")
    String description;
    @NotNull
    @BooleanFlag
    Boolean available;
    Long ownerId;
    ItemRequest request;
}
