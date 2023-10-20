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
    @Positive
    private final Long id;
    private final Long owner;
    @NotBlank(message = "The name cannot be empty")
    private String name;
    @NotBlank(message = "The description cannot be empty")
    private String description;
    @AssertTrue(message = "The item must be available for booking")
    @NotNull(message = "The availability cannot be null")
    private Boolean available;
    private Long request;
            // ID of the request for this item if item created as a response to the request
}
