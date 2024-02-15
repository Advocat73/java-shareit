package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

interface UserRepository {
    List<User> findAll();

    User findUserById(Long userId);

    User save(User user);

    User update(UserDto userDto, Long userId);

    void remove(Long userId);

    boolean existEmail(String email, Long userId);
}