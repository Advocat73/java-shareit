package ru.practicum.shareit.user;

import java.util.List;

interface UserService {
    List<User> getAllUsers();
    User getUser(Long userId);
    User saveUser(User user);
    User updateUser(User user, Long userId);
    void deleteUser(Long userId);
}
