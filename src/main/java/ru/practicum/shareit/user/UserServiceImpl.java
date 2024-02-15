package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на получение пользователей");
        return repository.findAll();
    }

    @Override
    public User getUser(Long userId) {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на получение пользователя с Id {}", userId);
        User user = repository.findUserById(userId);
        if (user == null)
            throw new NotFoundException("Нет пользователя с ID: " + userId);
        return user;
    }

    @Override
    public User saveUser(User user) {
        long l = -1;
        if (repository.existEmail(user.getEmail(), l))
            throw new DataConflictException("Пользователь с email: " + user.getEmail() + " уже существует");
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на сохранение нового пользователя");
        return repository.save(user);
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) {
        if (userId == null)
            throw new BadRequestException("Не указан Id пользователя при запросе Update");
        if (repository.existEmail(userDto.getEmail(), userId))
            throw new DataConflictException("Пользователь с email: " + userDto.getEmail() + " уже существует");
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на изменение данных пользователя с Id {}", userId);
        return repository.update(userDto, userId);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на удаление пользователя с Id {}", userId);
        repository.remove(userId);
    }
}
