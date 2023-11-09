package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.IBookingService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final IBookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoOut> createBooking(@RequestHeader(requestHeader) Long userId,
                                                       @RequestBody
                                                       @Valid BookingDtoIn bookingDtoIn) {
        log.info("createBooking request: userId = {}, bookingDtoIn = {}", userId, bookingDtoIn);
        return ResponseEntity.ok(bookingService.create(userId, bookingDtoIn));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> updateBooking(@PathVariable Long bookingId,
                                                       @RequestParam(name = "approved")
                                                       Boolean approved,
                                                       @RequestHeader(requestHeader) Long userId) {
        log.info("updateBooking request: bookingId = {}, approved = {}", bookingId, approved);
        return ResponseEntity.ok(bookingService.update(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> getBooking(@PathVariable Long bookingId,
                                                    @RequestHeader(requestHeader) Long userId) {
        log.info("getBooking request: bookingId = {}", bookingId);
        return ResponseEntity.ok(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoOut>> getAllForCurrentUser(
            @RequestHeader(requestHeader) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("getBookings request: userId = {}, state = {}", userId, state);
        return ResponseEntity.ok(bookingService.getAllBookingsForCurrentUserId(userId, from, size,
                State.fromString(state)));
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getAllForOwner(@RequestHeader(requestHeader) Long userId,
                                            @RequestParam(name = "state", defaultValue = "ALL")
                                            String state,
                                            @RequestParam(name = "from", defaultValue = "0")
                                            @PositiveOrZero Integer from,
                                            @RequestParam(name = "size", defaultValue = "10")
                                            @PositiveOrZero Integer size) {
        log.info("getBookings request: userId = {}, state = {}", userId, state);
        return ResponseEntity.ok(bookingService.getAllItemBookingsForOwnerId(userId, from, size,
                State.fromString(state)));
    }
}

