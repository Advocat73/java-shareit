package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private long userCounter = 0;

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User findUserById(Long userId) {
        for (User u : users) {
            if (u.getId().equals(userId))
                return u;
        }
        return null;
    }

    @Override
    public User save(User user) {
        user.setId(getId());
        users.add(user);
        return user;
    }

    @Override
    public User update(User user, Long userId) {
        User updatedUser = findUserById(userId);
        if (updatedUser == null)
            throw new NotFoundException("Нет пользователя с ID: " + userId);
        if (user.getEmail() != null)
            updatedUser.setEmail(user.getEmail());
        if (user.getName() != null)
            updatedUser.setName(user.getName());
        return updatedUser;
    }

    @Override
    public void remove(Long userId) {
        for (User u : users)
            if (u.getId().equals(userId)) {
                users.remove(u);
                return;
            }
    }

    @Override
    public boolean existEmail(User user, Long userId) {
        for (User u : findAll())
            if (u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), userId))
                return true;
        return false;
    }

    private Long getId() {
        return ++userCounter;
    }
}
