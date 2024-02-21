package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
    public static User fromUserDto(UserDto userDto) {
        User user = new User();
        if (userDto.getEmail() != null)
            user.setEmail(userDto.getEmail());
        if (userDto.getName() != null)
            user.setName(userDto.getName());
        return user;
    }
}
