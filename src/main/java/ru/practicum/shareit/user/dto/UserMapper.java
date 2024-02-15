package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static User fromUserDto(UserDto userDto, User user) {
        if (userDto.getEmail() != null)
            user.setEmail(userDto.getEmail());
        if (userDto.getName() != null)
            user.setName(userDto.getName());
        return user;
    }
}
