package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

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
