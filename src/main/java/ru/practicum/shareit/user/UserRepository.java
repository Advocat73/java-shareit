package ru.practicum.shareit.user;

import java.util.List;

interface UserRepository {
    List<User> findAll();

    User findUserById(Long userId);

    User save(User user);

    User update(User user, Long userId);

    void remove(Long userId);

    boolean existEmail(User user, Long userId);
}