package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(User user) {
        long l = -1;
        if (repository.existEmail(user, l))
            throw new DataConflictException("Пользователь с email: " + user.getEmail() + " уже существует");
        return repository.save(user);
    }

    @Override
    public User updateUser(User user, Long userId) {
        if (userId == null)
            throw new BadRequestException("Не указан Id пользователя при запросе Update");
        if (repository.existEmail(user, userId))
            throw new DataConflictException("Пользователь с email: " + user.getEmail() + " уже существует");
        return repository.update(user, userId);
    }
}
