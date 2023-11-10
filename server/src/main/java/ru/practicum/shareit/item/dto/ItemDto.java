package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
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