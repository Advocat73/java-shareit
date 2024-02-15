package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
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
