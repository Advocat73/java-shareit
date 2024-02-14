package ru.practicum.shareit.user;

import java.util.List;

interface UserRepository {
    List<User> findAll();

    User save(User user);

    boolean existEmail(User user);
}