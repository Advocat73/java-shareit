package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("USER_КОНТРОЛЛЕР: GET-запрос по эндпоинту /users");
        return userService.getAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("USER_КОНТРОЛЛЕР: GET-запрос по эндпоинту /users/{}", userId);
        return UserMapper.toUserDto(userService.getUser(userId));
    }

    @PostMapping
    public User saveNewUser(@Valid @RequestBody User user) {
        log.info("USER_КОНТРОЛЛЕР: POST-запрос по эндпоинту /users");
        return userService.saveUser(user);
    }

    @PatchMapping("/{userId}")
    public User update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("USER_КОНТРОЛЛЕР: PATCH-запрос по эндпоинту /users/{}", userId);
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("USER_КОНТРОЛЛЕР: DELETE-запрос по эндпоинту /users/{}", userId);
        userService.deleteUser(userId);
    }
}
