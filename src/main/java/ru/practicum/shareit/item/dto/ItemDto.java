package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    @Positive
    private final Long id;
    @NotBlank(message = "The name cannot be empty")
    private String name;
    private String description;
    private boolean available;
    private final Long owner; /*ID Владельца вещи*/
    private Long request; /*Ссылка на запрос другого пользователя*/
}