package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UpdateUser;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable(name = "userId") int userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable(name = "userId") int userId, @Valid @RequestBody UpdateUser updateUser) {
        return userService.updateUser(userId, updateUser);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@PathVariable(name = "userId") int userId) {
        return userService.deleteUser(userId);
    }
}
