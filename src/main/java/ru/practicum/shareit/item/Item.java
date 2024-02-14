package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    Long id;                //уникальный идентификатор вещи
    String name;            //краткое название;
    String description;     //развёрнутое описание
    boolean available;      //статус о том, доступна или нет вещь для аренды
    User owner;             //владелец вещи
    ItemRequest request;    /*если вещь была создана по запросу другого пользователя,
                            то в этом поле будет храниться ссылка на соответствующий запрос*/
}
