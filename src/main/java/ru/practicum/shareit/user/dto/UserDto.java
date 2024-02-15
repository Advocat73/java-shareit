package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserDto {
    long id;            //уникальный идентификатор пользователя
    String name;        //имя или логин пользователя
    String email;       /*адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты)*/
}
