package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на получение пользователей");
        return repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на получение пользователя с Id {}", userId);
        User user = repository.findUserById(userId);
        if (user == null)
            throw new NotFoundException("Нет пользователя с ID: " + userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        long l = -1;
        if (repository.existEmail(userDto.getEmail(), l))
            throw new DataConflictException("Пользователь с email: " + userDto.getEmail() + " уже существует");
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на сохранение нового пользователя");
        User user = UserMapper.fromUserDto(userDto);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        if (userId == null)
            throw new BadRequestException("Не указан Id пользователя при запросе Update");
        if (repository.existEmail(userDto.getEmail(), userId))
            throw new DataConflictException("Пользователь с email: " + userDto.getEmail() + " уже существует");
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на изменение данных пользователя с Id {}", userId);
        User user = UserMapper.fromUserDto(userDto);
        return UserMapper.toUserDto(repository.update(user, userId));
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на удаление пользователя с Id {}", userId);
        repository.remove(userId);
    }
}
