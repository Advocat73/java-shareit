package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Long userId);

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto user, Long userId);

    void deleteUser(Long userId);
}
