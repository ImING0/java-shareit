package ru.practicum.shareit.booking;

public enum Status {
    WAITING, //— новое бронирование, ожидает одобрения
    APPROVED, //—бронирование подтверждено владельцем
    REJECTED, //— бронированиеотклонено владельцем
    CANCELED //— бронирование отменено создателем
}
