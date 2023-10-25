package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    @Positive
    private Long id;
    private Long owner;
    @NotBlank(message = "The name cannot be empty")
    private String name;
    @NotBlank(message = "The description cannot be empty")
    private String description;
    @AssertTrue(message = "The item must be available for booking")
    @NotNull(message = "The availability cannot be null")
    private Boolean available;
    private Long request; /*Ссылка на запрос другого пользователя
     (если вещь создана по запросу)*/
}