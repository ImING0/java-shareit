package ru.practicum.shareit.item.exeption;

public class IllegalNameOrDescriptionOfItemException extends RuntimeException{
    public IllegalNameOrDescriptionOfItemException(String message) {
        super(message);
    }
}
