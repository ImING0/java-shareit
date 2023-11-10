package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingDtoOut;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoOut {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId; /*Ссылка на запрос другого пользователя
     (если вещь создана по запросу)*/
    private BookingDtoOut lastBooking;
    private BookingDtoOut nextBooking;
    private List<CommentDtoOut> comments;
}
