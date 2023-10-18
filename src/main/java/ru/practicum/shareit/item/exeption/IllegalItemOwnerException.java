package ru.practicum.shareit.item.exeption;

public class IllegalItemOwnerException extends RuntimeException{
    public IllegalItemOwnerException(String message) {
        super(message);
    }
}
