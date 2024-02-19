package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private long userCounter = 0;

    @Override
    public List<User> findAll() {
        log.info("USER_ХРАНИЛИЩЕ: Получение всех пользователя");
        return users;
    }

    @Override
    public User findUserById(Long userId) {
        log.info("USER_ХРАНИЛИЩЕ: Получение пользователя с id {}", userId);
        for (User u : users) {
            if (u.getId().equals(userId))
                return u;
        }
        return null;
    }

    @Override
    public User save(User user) {
        user.setId(getId());
        log.info("USER_ХРАНИЛИЩЕ: Сохранение пользователя с id {}", user.getId());
        users.add(user);
        return user;
    }

    @Override
    public User update(UserDto userDto, Long userId) {
        User updatedUser = findUserById(userId);
        if (updatedUser == null)
            throw new NotFoundException("Нет пользователя с ID: " + userId);
        log.info("USER_ХРАНИЛИЩЕ: Изменение пользователя с id {}", userId);
        return UserMapper.fromUserDto(userDto, updatedUser);
    }

    @Override
    public void remove(Long userId) {
        log.info("USER_ХРАНИЛИЩЕ: Удаление пользователя с id {}", userId);
        for (User u : users)
            if (u.getId().equals(userId)) {
                users.remove(u);
                return;
            }
    }

    @Override
    public boolean existEmail(String email, Long userId) {
        log.info("USER_ХРАНИЛИЩЕ: Проверка email {}", email);
        for (User u : findAll())
            if (u.getEmail().equals(email) && !Objects.equals(u.getId(), userId))
                return true;
        return false;
    }

    private Long getId() {
        return ++userCounter;
    }
}
