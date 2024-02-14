package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Item;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User saveNewUser(@Valid @RequestBody User user) {
        return userService.saveUser(user);
    }

    @PatchMapping("/{userId}")
    public User update(@RequestBody User user, @PathVariable Long userId)  {
        return userService.updateUser(user, userId);
    }
}
