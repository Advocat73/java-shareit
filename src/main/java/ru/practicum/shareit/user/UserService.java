package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addNewUser(UserDto userDto);

    UserDto updateUser(UserDto user, Long userId);

    List<UserDto> getAllUsers();

    UserDto getUser(Long userId);


    void deleteUser(Long userId);
}
