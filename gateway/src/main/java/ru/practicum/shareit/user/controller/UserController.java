package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<UserDtoOut> createUser(@Valid @RequestBody UserDtoIn userDtoIn) {
        log.info("createUser request: user = {}", userDtoIn);
        return ResponseEntity.ok(userClient.create(userDtoIn)
                .getBody());
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId,
                                             @RequestBody UserDtoIn userDtoIn) {
        log.info("updateUser request: userId = {}, user = {}", userId, userDtoIn);
        return ResponseEntity.ok(userClient.update(userDtoIn, userId)
                .getBody());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        userClient.delete(userId);
        log.info("deleteUser request: userId = {}", userId);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") Long userId) {
        log.info("getUserById request: userId = {}", userId);
        return ResponseEntity.ok(userClient.getById(userId)
                .getBody());
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("getAllUsers request");
        return ResponseEntity.ok(userClient.getAll()
                .getBody());
    }
}
