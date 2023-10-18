package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exeption.IllegalItemOwnerException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.util.ErrorResponse;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit.item.controller")
public class ItemControllerExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("Missing request header", ex);
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(value = {IllegalItemOwnerException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIllegalItemOwnerException(IllegalItemOwnerException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .build();
    }
}
