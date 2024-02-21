package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    long id;
    @NotNull
    String name;
    @NotNull
    @Email(message = "Передан некорректный e-mail адрес")
    String email;
}
