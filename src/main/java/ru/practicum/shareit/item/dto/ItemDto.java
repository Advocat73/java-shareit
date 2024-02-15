package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    Long id;                //уникальный идентификатор вещи
    String name;            //краткое название;
    String description;     //развёрнутое описание
    Boolean available;      //статус о том, доступна или нет вещь для аренды
    Long ownerId;           //владелец вещи
    Long requestId;         /*если вещь была создана по запросу другого пользователя,
                            то в этом поле будет храниться ссылка на соответствующий запрос*/

    public ItemDto(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
