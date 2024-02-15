package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    Long id;            //уникальный идентификатор пользователя
    String name;        //имя или логин пользователя
    @NotNull
    @Email(message = "Передан некорректный e-mail адрес")
    String email;       /*адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты)*/
}
