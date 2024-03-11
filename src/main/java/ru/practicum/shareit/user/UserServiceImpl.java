package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    private final UserMapper userMapper;

    @Override
    public UserDto addNewUser(UserDto userDto) {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на сохранение нового пользователя");
        return addUser(userMapper.fromUserDto(userDto));
    }

    @Override
    public UserDto updateUser(UserDto updatedUserDto, Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + userId));
        User updatedUser = userMapper.fromUserDto(updatedUserDto);
        if (updatedUser.getEmail() != null)
            user.setEmail(updatedUser.getEmail());
        if (updatedUser.getName() != null)
            user.setName(updatedUser.getName());
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на изменение данных пользователя с Id {}", userId);
        return addUser(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на получение пользователей");
        return repository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException("Нет пользователя с ID: " + userId));
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на получение пользователя с Id {}", userId);
        return userMapper.toUserDto(user);
    }


    @Override
    public void deleteUser(Long userId) {
        log.info("USER_СЕРВИС: Отправлен запрос к хранилищу на удаление пользователя с Id {}", userId);
        repository.deleteById(userId);
    }

    private UserDto addUser(User user) {
        try {
            return userMapper.toUserDto(repository.save(user));
        } catch (RuntimeException e) {
            throw new DataConflictException("Пользователь по имени " + user.getName() + " не добавлен в БД из-за неверных данных");
        }
    }
}
