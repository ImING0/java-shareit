package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<BookingDtoOut> createBooking(@RequestHeader(requestHeader) Long userId,
                                                       @RequestBody
                                                       @Valid BookingDtoIn bookingDtoIn) {
        log.info("createBooking request: userId = {}, bookingDtoIn = {}", userId, bookingDtoIn);
        return ResponseEntity.ok(bookingClient.create(userId, bookingDtoIn)
                .getBody());
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> updateBooking(@PathVariable Long bookingId,
                                                       @RequestParam(name = "approved")
                                                       Boolean approved,
                                                       @RequestHeader(requestHeader) Long userId) {
        log.info("updateBooking request: bookingId = {}, approved = {}", bookingId, approved);
        return ResponseEntity.ok(bookingClient.update(bookingId, approved, userId)
                .getBody());
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> getBooking(@PathVariable Long bookingId,
                                                    @RequestHeader(requestHeader) Long userId) {
        log.info("getBooking request: bookingId = {}", bookingId);
        return ResponseEntity.ok(bookingClient.getBookingById(bookingId, userId)
                .getBody());
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoOut>> getAllForCurrentUser(
            @RequestHeader(requestHeader) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("getBookings request: userId = {}, state = {}", userId, state);
        return ResponseEntity.ok(bookingClient.getAllBookingsForCurrentUserId(userId, from, size,
                        State.fromString(state))
                .getBody());
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoOut>> getAllForOwner(
            @RequestHeader(requestHeader) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("getBookings request: userId = {}, state = {}", userId, state);
        return ResponseEntity.ok(bookingClient.getAllItemBookingsForOwnerId(userId, from, size,
                        State.fromString(state))
                .getBody());
    }
}

