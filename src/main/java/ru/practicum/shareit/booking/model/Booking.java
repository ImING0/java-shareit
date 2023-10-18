package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class Booking {
    private final Long id; // ID of the booking
    private LocalDateTime start; // Start time of the booking
    private LocalDateTime end;  // End time of the booking
    private Long item; // ID of the item being booked
    private Long booker; // ID of the user who booked the item
    private Status status;  // Status of the booking
}
