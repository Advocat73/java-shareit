package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public interface UserMapper {
    UserDto toUserDto(User user);

    User fromUserDto(UserDto userDto);
}
