package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String baseUrl) {
        super(baseUrl + API_PREFIX);
    }

    public ResponseEntity<BookingDtoOut> create(Long userId,
                                                BookingDtoIn bookingDtoIn) {
        return post("", bookingDtoIn, userId, BookingDtoOut.class);
    }

    public ResponseEntity<BookingDtoOut> update(Long bookingId,
                                                Boolean approved,
                                                Long userId) {
        return patch("/" + bookingId + "?approved=" + approved.toString(), null, userId, BookingDtoOut.class);
    }

    public ResponseEntity<BookingDtoOut> getBookingById(Long bookingId,
                                                        Long userId) {
        return get("/" + bookingId, userId, Map.of(), BookingDtoOut.class);
    }

    public ResponseEntity<List<BookingDtoOut>> getAllBookingsForCurrentUserId(Long userId,
                                                                              Integer from,
                                                                              Integer size,
                                                                              State state) {
        return getAll("", userId, Map.of("from", from.toString(), "size", size.toString(), "state",
                        state.toString()),
                BookingDtoOut[].class);
    }

    public ResponseEntity<List<BookingDtoOut>> getAllItemBookingsForOwnerId(Long userId,
                                                                            Integer from,
                                                                            Integer size,
                                                                            State state) {
        return getAll("/owner", userId, Map.of("from", from.toString(), "size", size.toString(), "state",
                        state.toString()),
                BookingDtoOut[].class);
    }
}
