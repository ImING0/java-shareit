package ru.practicum.shareit.item.model;

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
public class Item {
    private final Long id;
    private final Long owner;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    // ID of the request for this item if item created as a response to the request
}
