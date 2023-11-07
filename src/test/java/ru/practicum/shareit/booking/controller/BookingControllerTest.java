package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookingControllerTest {
    private final String requestHeader = "X-Sharer-User-Id";
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User ownerOfFirstItem;
    private Item itemOfFirstUser;
    private User bookerOfFirstItem;
    private Booking bookingOfFirstItem;
    private User otherUser1;

    @BeforeEach
    void setUp() {
        ownerOfFirstItem = User.builder()
                .name("Alex")
                .email("alexPositive@mail.ru")
                .build();
        itemOfFirstUser = Item.builder()
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();
        bookerOfFirstItem = User.builder()
                .name("Oleg")
                .email("horoshayaRabotaOleg@mail.ru")
                .build();
        bookingOfFirstItem = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now()
                        .plusDays(1))
                .status(Status.WAITING)
                .build();
        otherUser1 = User.builder()
                .name("Sanek")
                .email("sanyaTiVPoryadke@sotka.ru")
                .build();
    }

    @Test
    void createBooking_WhenOwner_ReturnResourceNotFound() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        Item savedItem = itemOfFirstUser;
        savedItem.setOwner(savedOwner.getId());
        savedItem = itemRepository.save(savedItem);
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.now()
                        .plusSeconds(2))
                .end(LocalDateTime.now()
                        .plusDays(1))
                .build();
        webTestClient.post()
                .uri("/bookings")
                .header(requestHeader, String.valueOf(savedOwner.getId()))
                .bodyValue(bookingDtoIn)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void createBooking_WhenItemUnavailable_ReturnBadRequest() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        Item savedItem = itemOfFirstUser;
        savedItem.setOwner(savedOwner.getId());
        savedItem.setAvailable(false);
        savedItem = itemRepository.save(savedItem);
        User savedBooker = userRepository.save(bookerOfFirstItem);
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.now()
                        .plusSeconds(2))
                .end(LocalDateTime.now()
                        .plusDays(1))
                .build();
        webTestClient.post()
                .uri("/bookings")
                .header(requestHeader, String.valueOf(savedBooker.getId()))
                .bodyValue(bookingDtoIn)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void createWhenValid_ReturnBooking() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        Item savedItem = itemOfFirstUser;
        User savedBooker = userRepository.save(bookerOfFirstItem);
        savedItem.setOwner(savedOwner.getId());
        savedItem = itemRepository.save(savedItem);
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.now()
                        .plusSeconds(2))
                .end(LocalDateTime.now()
                        .plusDays(1))
                .build();

        BookingDtoOut bookingDtoOut = webTestClient.post()
                .uri("/bookings")
                .header(requestHeader, String.valueOf(savedBooker.getId()))
                .bodyValue(bookingDtoIn)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(bookingDtoIn.getItemId(), bookingDtoOut.getItemId());
        assertEquals(bookingDtoIn.getStart(), bookingDtoOut.getStart());
        assertEquals(bookingDtoIn.getEnd(), bookingDtoOut.getEnd());
        assertEquals(Status.WAITING, bookingDtoOut.getStatus());
    }

    @Test
    void updateBooking_WhenNotOwner_ReturnResourceNotFound() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        User savedBooker = userRepository.save(bookerOfFirstItem);
        itemOfFirstUser.setOwner(savedOwner.getId());
        Item savedItem = itemRepository.save(itemOfFirstUser);
        bookingOfFirstItem.setItem(savedItem);
        bookingOfFirstItem.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(bookingOfFirstItem);
        User otherUser = userRepository.save(otherUser1);

        webTestClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}")
                        .queryParam("approved", "true")
                        .build(savedBooking.getId()))
                .header(requestHeader, String.valueOf(otherUser.getId()))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateBooking_WhenOwner_ReturnBooking() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        User savedBooker = userRepository.save(bookerOfFirstItem);
        itemOfFirstUser.setOwner(savedOwner.getId());
        Item savedItem = itemRepository.save(itemOfFirstUser);
        bookingOfFirstItem.setItem(savedItem);
        bookingOfFirstItem.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(bookingOfFirstItem);

        BookingDtoOut bookingDtoOut = webTestClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}")
                        .queryParam("approved", "true")
                        .build(savedBooking.getId()))
                .header(requestHeader, String.valueOf(savedOwner.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(savedBooking.getId(), bookingDtoOut.getId());
        assertEquals(Status.APPROVED, bookingDtoOut.getStatus());
    }

    @Test
    void updateBooking_WhenNotWaiting_ReturnBadRequest() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        User savedBooker = userRepository.save(bookerOfFirstItem);
        itemOfFirstUser.setOwner(savedOwner.getId());
        Item savedItem = itemRepository.save(itemOfFirstUser);
        bookingOfFirstItem.setItem(savedItem);
        bookingOfFirstItem.setBooker(savedBooker);
        bookingOfFirstItem.setStatus(Status.APPROVED);
        Booking savedBooking = bookingRepository.save(bookingOfFirstItem);

        webTestClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}")
                        .queryParam("approved", "true")
                        .build(savedBooking.getId()))
                .header(requestHeader, String.valueOf(savedOwner.getId()))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void getBooking_WhenNotOwnerAndNotBooker_ThrowResourceNotFound() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        User savedBooker = userRepository.save(bookerOfFirstItem);
        itemOfFirstUser.setOwner(savedOwner.getId());
        Item savedItem = itemRepository.save(itemOfFirstUser);
        bookingOfFirstItem.setItem(savedItem);
        bookingOfFirstItem.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(bookingOfFirstItem);
        User otherUser = userRepository.save(otherUser1);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}")
                        .build(savedBooking.getId()))
                .header(requestHeader, String.valueOf(otherUser.getId()))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getBooking_WhenBooker_ReturnBooking() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        User savedBooker = userRepository.save(bookerOfFirstItem);
        itemOfFirstUser.setOwner(savedOwner.getId());
        Item savedItem = itemRepository.save(itemOfFirstUser);
        bookingOfFirstItem.setItem(savedItem);
        bookingOfFirstItem.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(bookingOfFirstItem);

        BookingDtoOut bookingDtoOut = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}")
                        .build(savedBooking.getId()))
                .header(requestHeader, String.valueOf(savedBooker.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(savedBooking.getId(), bookingDtoOut.getId());
        assertEquals(savedBooking.getStatus(), bookingDtoOut.getStatus());
    }

    @Test
    void getBooking_WhenOwner_ReturnBooking() {
        User savedOwner = userRepository.save(ownerOfFirstItem);
        User savedBooker = userRepository.save(bookerOfFirstItem);
        itemOfFirstUser.setOwner(savedOwner.getId());
        Item savedItem = itemRepository.save(itemOfFirstUser);
        bookingOfFirstItem.setItem(savedItem);
        bookingOfFirstItem.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(bookingOfFirstItem);

        BookingDtoOut bookingDtoOut = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}")
                        .build(savedBooking.getId()))
                .header(requestHeader, String.valueOf(savedOwner.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(savedBooking.getId(), bookingDtoOut.getId());
        assertEquals(savedBooking.getStatus(), bookingDtoOut.getStatus());
    }

    @Test
    void getAllForCurrentUser_WhenDifferentBookingState_ReturnBookings() {
        User ownerOfAllItems = userRepository.save(ownerOfFirstItem);
        User bookerForAllItems = userRepository.save(bookerOfFirstItem);
        setItemsAndBookingsForCurrentUserTest(ownerOfAllItems, bookerForAllItems);

        /*Сделаем запрос без парамметра State, должны все вернуться*/

        List<BookingDtoOut> allBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .build())
                .header(requestHeader, String.valueOf(bookerForAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(5, allBookings.size());

        /*State current, вернется только 1*/
        List<BookingDtoOut> currentBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state", "CURRENT")
                        .build())
                .header(requestHeader, String.valueOf(bookerForAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, currentBookings.size());
        assertEquals(Status.APPROVED, currentBookings.get(0)
                .getStatus());
        assertEquals("Current Клей Нюхательный", currentBookings.get(0)
                .getItem()
                .getName());

        /*State past, вернется только 1*/
        List<BookingDtoOut> pastBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state", "PAST")
                        .build())
                .header(requestHeader, String.valueOf(bookerForAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, pastBookings.size());
        assertEquals(Status.APPROVED, pastBookings.get(0)
                .getStatus());
        assertEquals("Past Старинные часы карманные", pastBookings.get(0)
                .getItem()
                .getName());

        /*State future, вернется 3*/
        List<BookingDtoOut> futureBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state", "FUTURE")
                        .build())
                .header(requestHeader, String.valueOf(bookerForAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(3, futureBookings.size());

        /*State waiting, вернется только 1*/
        List<BookingDtoOut> waitingBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state", "WAITING")
                        .build())
                .header(requestHeader, String.valueOf(bookerForAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, waitingBookings.size());
        assertEquals(Status.WAITING, waitingBookings.get(0)
                .getStatus());
        assertEquals("Waiting Книга 'Ожидание встречи'", waitingBookings.get(0)
                .getItem()
                .getName());

        /*State rejected, вернется только 1*/
        List<BookingDtoOut> rejectedBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state", "REJECTED")
                        .build())
                .header(requestHeader, String.valueOf(bookerForAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, rejectedBookings.size());
        assertEquals(Status.REJECTED, rejectedBookings.get(0)
                .getStatus());
        assertEquals("Rejected Пылесос 'Циклон'", rejectedBookings.get(0)
                .getItem()
                .getName());
    }

    @Test
    void getAllForCurrentUser_WhenUserNotFound_ReturnResourceNotFound() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .build())
                .header(requestHeader, String.valueOf(1L))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getAllForOwner_WhenDifferentBookingState_ReturnBookings() {
        User ownerOfAllItems = userRepository.save(ownerOfFirstItem);
        User bookerForAllItems = userRepository.save(bookerOfFirstItem);
        setItemsAndBookingsForCurrentUserTest(ownerOfAllItems, bookerForAllItems);

        /*Сделаем запрос без парамметра State, должны все вернуться*/

        List<BookingDtoOut> allBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .build())
                .header(requestHeader, String.valueOf(ownerOfAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(5, allBookings.size());

        /*State current, вернется только 1*/
        List<BookingDtoOut> currentBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state", "CURRENT")
                        .build())
                .header(requestHeader, String.valueOf(ownerOfAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, currentBookings.size());
        assertEquals(Status.APPROVED, currentBookings.get(0)
                .getStatus());
        assertEquals("Current Клей Нюхательный", currentBookings.get(0)
                .getItem()
                .getName());

        /*State past, вернется только 1*/
        List<BookingDtoOut> pastBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state", "PAST")
                        .build())
                .header(requestHeader, String.valueOf(ownerOfAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, pastBookings.size());
        assertEquals(Status.APPROVED, pastBookings.get(0)
                .getStatus());
        assertEquals("Past Старинные часы карманные", pastBookings.get(0)
                .getItem()
                .getName());

        /*State future, вернется 3*/
        List<BookingDtoOut> futureBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state", "FUTURE")
                        .build())
                .header(requestHeader, String.valueOf(ownerOfAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(3, futureBookings.size());

        /*State waiting, вернется только 1*/
        List<BookingDtoOut> waitingBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state", "WAITING")
                        .build())
                .header(requestHeader, String.valueOf(ownerOfAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, waitingBookings.size());
        assertEquals(Status.WAITING, waitingBookings.get(0)
                .getStatus());
        assertEquals("Waiting Книга 'Ожидание встречи'", waitingBookings.get(0)
                .getItem()
                .getName());

        /*State rejected, вернется только 1*/
        List<BookingDtoOut> rejectedBookings = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state", "REJECTED")
                        .build())
                .header(requestHeader, String.valueOf(ownerOfAllItems.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookingDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, rejectedBookings.size());
        assertEquals(Status.REJECTED, rejectedBookings.get(0)
                .getStatus());
        assertEquals("Rejected Пылесос 'Циклон'", rejectedBookings.get(0)
                .getItem()
                .getName());
    }

    private void setItemsAndBookingsForCurrentUserTest(User ownerOfItems,
                                                       User bookerOfItems) {
        /*Создадим 5 вещей для последующего бронирования.*/
        Long ownerId = ownerOfItems.getId();
        Long bookerId = bookerOfItems.getId();

        Item itemForCurrentState = Item.builder()
                .owner(ownerId)
                .name("Current Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();
        itemForCurrentState = itemRepository.save(itemForCurrentState);

        Item itemForPastState = Item.builder()
                .owner(ownerId)
                .name("Past Старинные часы карманные")
                .description(
                        "Элегантные карманные часы с цепочкой, отлично сохранившиеся, вековая история в каждом щелчке их механизма.")
                .available(false)
                .build();
        itemForPastState = itemRepository.save(itemForPastState);

        Item itemForFutureState = Item.builder()
                .owner(ownerId)
                .name("Future Голографический проектор")
                .description(
                        "Инновационный гаджет для проецирования 3D-голограмм в воздухе, будущее визуальных презентаций.")
                .available(true)
                .build();
        itemForFutureState = itemRepository.save(itemForFutureState);

        Item itemForWaitingState = Item.builder()
                .owner(ownerId)
                .name("Waiting Книга 'Ожидание встречи'")
                .description(
                        "Пронзительный роман о жизни, любви и муках ожидания, который не оставит вас равнодушным.")
                .available(true)
                .build();
        itemForWaitingState = itemRepository.save(itemForWaitingState);

        Item itemForRejectedState = Item.builder()
                .owner(ownerId)
                .name("Rejected Пылесос 'Циклон'")
                .description(
                        "Мощный пылесос с инновационной системой фильтрации, но, к сожалению, отклонённый потребителями из-за его размера.")
                .available(false)
                .build();
        itemForRejectedState = itemRepository.save(itemForRejectedState);

        /*Теперь проставим бронирования для каждой вещи, пускай буккер будет один и тот же.*/

        Booking bookingWithCurrentState = Booking.builder()
                .start(LocalDateTime.now()
                        .minusDays(1))
                .end(LocalDateTime.now()
                        .plusDays(1))
                .item(itemForCurrentState)
                .booker(bookerOfItems)
                .status(Status.APPROVED)
                .build();
        bookingWithCurrentState = bookingRepository.save(bookingWithCurrentState);

        Booking bookingWithPastState = Booking.builder()
                .start(LocalDateTime.now()
                        .minusDays(122))
                .end(LocalDateTime.now()
                        .minusDays(2))
                .item(itemForPastState)
                .booker(bookerOfItems)
                .status(Status.APPROVED)
                .build();
        bookingWithPastState = bookingRepository.save(bookingWithPastState);

        Booking bookingWithFutureState = Booking.builder()
                .start(LocalDateTime.now()
                        .plusDays(2))
                .end(LocalDateTime.now()
                        .plusDays(122))
                .item(itemForFutureState)
                .booker(bookerOfItems)
                .status(Status.APPROVED)
                .build();
        bookingWithFutureState = bookingRepository.save(bookingWithFutureState);

        Booking bookingWithWaitingState = Booking.builder()
                .start(LocalDateTime.now()
                        .plusDays(2))
                .end(LocalDateTime.now()
                        .plusDays(122))
                .item(itemForWaitingState)
                .booker(bookerOfItems)
                .status(Status.WAITING)
                .build();
        bookingWithWaitingState = bookingRepository.save(bookingWithWaitingState);

        Booking bookingWithRejectedState = Booking.builder()
                .start(LocalDateTime.now()
                        .plusDays(2))
                .end(LocalDateTime.now()
                        .plusDays(122))
                .item(itemForRejectedState)
                .booker(bookerOfItems)
                .status(Status.REJECTED)
                .build();
        bookingWithRejectedState = bookingRepository.save(bookingWithRejectedState);
    }
}