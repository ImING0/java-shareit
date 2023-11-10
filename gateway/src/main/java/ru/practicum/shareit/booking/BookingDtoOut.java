package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoOut {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoOut item;
    private UserDtoOut booker;
    private Status status;

    public Long getItemId() {
        return item.getId();
    }

    public long getBookerId() {
        return booker.getId();
    }
}
