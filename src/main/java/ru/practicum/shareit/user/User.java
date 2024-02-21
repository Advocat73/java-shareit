package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class User {
    Long id;
    @NotNull
    String name;
    @NotNull
    @Email(message = "Передан некорректный e-mail адрес")
    String email;
}
