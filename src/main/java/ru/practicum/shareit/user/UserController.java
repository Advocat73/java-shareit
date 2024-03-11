package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("USER_КОНТРОЛЛЕР: POST-запрос по эндпоинту /users");
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @NotNull(message = "Не указан Id пользователя при запросе на изменение данных")
                              @PathVariable Long userId) {
        log.info("USER_КОНТРОЛЛЕР: PATCH-запрос по эндпоинту /users/{}", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("USER_КОНТРОЛЛЕР: GET-запрос по эндпоинту /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@NotNull(message = "Не указан Id пользователя при запросе данных пользователя")
                           @PathVariable Long userId) {
        log.info("USER_КОНТРОЛЛЕР: GET-запрос по эндпоинту /users/{}", userId);
        return userService.getUser(userId);
    }


    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("USER_КОНТРОЛЛЕР: DELETE-запрос по эндпоинту /users/{}", userId);
        userService.deleteUser(userId);
    }
}
