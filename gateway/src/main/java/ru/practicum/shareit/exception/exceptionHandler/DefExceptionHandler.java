package ru.practicum.shareit.exception.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.practicum.shareit.util.ErrorResponse;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit")
public class DefExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("WebClient exception", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ex.getRawStatusCode())
                .build();

        // Создаем ResponseEntity с нужным статусом и телом ответа
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getRawStatusCode()));
    }

    /*@ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException ex) {
        log.error("Bad request", ex);
        return ErrorResponse.builder()
                .error(ex.getMessage())
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
    }

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

    @ExceptionHandler(value = {IllegalOwnerException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIllegalItemOwnerException(IllegalOwnerException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .build();
    }*/
}
