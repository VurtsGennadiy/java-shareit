package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserCreateDto newUser) {
        return userService.createNewUser(newUser);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable @Positive Long userId) {
        return userService.getUser(userId);
    }
}
