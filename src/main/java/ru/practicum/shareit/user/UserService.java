package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUser(Long userId);
    User saveUser(User user);
    User updateUser(UserDto user, Long userId);
    void deleteUser(Long userId);
}
