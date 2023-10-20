package ru.practicum.shareit.exception;

public class IllegalNameOrDescriptionException extends RuntimeException {
    public IllegalNameOrDescriptionException(String message) {
        super(message);
    }
}
