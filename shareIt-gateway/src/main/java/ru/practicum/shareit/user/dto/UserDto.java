package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private long id;
    @NotNull
    private String name;
    @NotNull
    @Email(message = "Передан некорректный e-mail адрес")
    private String email;
}
