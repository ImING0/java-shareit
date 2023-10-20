package ru.practicum.shareit.exception.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.IllegalOwnerException;
import ru.practicum.shareit.exception.ResourceAlreadyExistsException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.util.ErrorResponse;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit")
public class DefExceptionHandler {
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("Missing request header", ex);
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(ResourceNotFoundException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(value = {ResourceAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserEmailAlreadyExistException(ResourceAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .build();
    }

    @ExceptionHandler(value = {IllegalOwnerException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIllegalItemOwnerException(IllegalOwnerException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .build();
    }
}
