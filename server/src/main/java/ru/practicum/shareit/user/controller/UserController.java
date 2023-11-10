package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.impl.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        log.info("createUser request: user = {}", userDto);
        return ResponseEntity.ok(userService.create(userDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") Long userId,
                                              @RequestBody UserDto userDto) {
        log.info("updateUser request: userId = {}, user = {}", userId, userDto);
        return ResponseEntity.ok(userService.update(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.delete(userId);
        log.info("deleteUser request: userId = {}", userId);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") Long userId) {
        log.info("getUserById request: userId = {}", userId);
        return ResponseEntity.ok(userService.getById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("getAllUsers request");
        return ResponseEntity.ok(userService.getAll());
    }
}
