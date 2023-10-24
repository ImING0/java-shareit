package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@Entity
public class Item {
    @Id
    @Column(name = "ID")
    private final Long id;
    @Column(name = "OWNER")
    private final Long owner;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "AVAILABLE")
    private Boolean available;
    @Column(name = "REQUEST")
    private Long request;
    // ID of the request for this item if item created as a response to the request
}
