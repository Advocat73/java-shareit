package ru.practicum.shareit.item;

import jdk.jfr.BooleanFlag;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {
    Long id;                //уникальный идентификатор вещи
    @NotBlank(message = "Название вещи не может быть пустым")
    String name;            //краткое название;
    @NotBlank(message = "Должно быть описание вещи")
    String description;     //развёрнутое описание
    @NotNull
    @BooleanFlag
    Boolean available;      //статус о том, доступна или нет вещь для аренды
    Long ownerId;          //владелец вещи
    ItemRequest request;    /*если вещь была создана по запросу другого пользователя,
                            то в этом поле будет храниться ссылка на соответствующий запрос*/
}
