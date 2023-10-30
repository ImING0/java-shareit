package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.BadRequestException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    /**
     * Получить статус бронирования по его строковому представлению
     *
     * @param state строковое представление статуса
     * @return статус бронирования
     * @throws BadRequestException если статус не найден
     */
    public static State fromString(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }
}
