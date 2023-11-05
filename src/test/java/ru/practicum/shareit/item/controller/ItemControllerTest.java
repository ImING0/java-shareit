package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    private User user1;
    private User user2;
    private User user3;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private CommentDtoIn commentDtoIn1;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name("Alex")
                .email("alex@mail.com")
                .build();
        user2 = User.builder()
                .name("Brain")
                .email("brainDit@gmail.com")
                .build();
        user3 = User.builder()
                .name("Ержан")
                .email("vstavaierjan@mail.ru")
                .build();
        itemDto1 = ItemDto.builder()
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();
        itemDto2 = ItemDto.builder()
                .name("Насвай")
                .description("Хорошая замена клею")
                .available(true)
                .build();
        commentDtoIn1 = CommentDtoIn.builder()
                .text("Первый комментарий")
                .build();
    }

    /*@BeforeEach
    void tearDown() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }*/

    @Test
    void createItem_WhenValid_ReturnItemDto() {
        Long itemId = 1L;
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertTrue(itemDtoOut.getId() > 0);
        assertEquals(userId, itemDtoOut.getOwner());
        assertEquals(itemDto.getName(), itemDtoOut.getName());
        assertEquals(itemDto.getDescription(), itemDtoOut.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtoOut.getAvailable());
    }

    @Test
    void create_WhenInvalidData_ReturnBadRequest() {
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();
        ItemDto itemDto = itemDto1;
        itemDto.setDescription(null);
        webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void create_WhenUserNotFound_ReturnNotFound() {
        Long userId = 666L;
        ItemDto itemDto = itemDto1;
        webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateItem_WhenValid_ReturnUpdated() {
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        Long itemId = itemDtoOut.getId();
        assertTrue(itemDtoOut.getId() > 0);

        ItemDto itemToUpdate = itemDto2;
        ItemDto itemDtoUpdated = webTestClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemToUpdate)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(itemId, itemDtoUpdated.getId());
        assertEquals(userId, itemDtoUpdated.getOwner());
        assertEquals(itemToUpdate.getName(), itemDtoUpdated.getName());
        assertEquals(itemToUpdate.getDescription(), itemDtoUpdated.getDescription());
        assertEquals(itemToUpdate.getAvailable(), itemDtoUpdated.getAvailable());
    }

    @Test
    void updateItem_WhenAllFieldsAreNull_ReturnBadRequest() {
        Long itemId = 1L;
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertTrue(itemDtoOut.getId() > 0);

        ItemDto itemToUpdate = itemDto2;
        itemToUpdate.setName(null);
        itemToUpdate.setDescription(null);
        itemToUpdate.setAvailable(null);
        webTestClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemToUpdate)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void update_WhenUserNotFound_ReturnNotFound() {
        Long incorrectUserId = 666L;
        Long itemId = 1L;
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertTrue(itemDtoOut.getId() > 0);

        ItemDto itemToUpdate = itemDto2;
        webTestClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(incorrectUserId))
                .bodyValue(itemToUpdate)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void update_WhenItemNotFound_ReturnNotFound() {

        Long itemId = 1L;
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertTrue(itemDtoOut.getId() > 0);

        ItemDto itemToUpdate = itemDto2;
        webTestClient.patch()
                .uri("/items/{itemId}", 2L)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemToUpdate)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void update_WhenNotOwner_ReturnForbidden() {
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        Long userId = savedUser1.getId();
        Long wrongOwnerId = savedUser2.getId();
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        Long itemId = itemDtoOut.getId();
        assertTrue(itemDtoOut.getId() > 0);

        ItemDto itemToUpdate = itemDto2;
        webTestClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(wrongOwnerId))
                .bodyValue(itemToUpdate)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void getItemByIdWithoutBookingAndComments_WhenOwner_ReturnItemDto() {
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        Long itemId = itemDtoOut.getId();

        assertTrue(itemDtoOut.getId() > 0);

        ItemDto itemDtoGet = webTestClient.get()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();

        assertEquals(itemId, itemDtoGet.getId());
        assertEquals(userId, itemDtoGet.getOwner());
        assertEquals(itemDto.getName(), itemDtoGet.getName());
        assertEquals(itemDto.getDescription(), itemDtoGet.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtoGet.getAvailable());
        assertNull(itemDtoGet.getLastBooking());
        assertNull(itemDtoGet.getNextBooking());
        assertTrue(itemDtoGet.getComments()
                .isEmpty());
    }

    @Test
    void getItemByIdWithBookingsAndComments_WhenOwnerAndNotOwner_ReturnItemDto() {
        /*Сначала создадим 3 юзеров, предмет, добавим брони и два коммента
         * Потом запросим предмет владельцем, а потом не владельцем. У не владельца
         * последних броней быть не должно, комменты будут там и там. */
        User ownerOfItem = userRepository.save(user1);
        Long ownerId = ownerOfItem.getId();
        User userWhoBooked = userRepository.save(user2);
        User otherUser = userRepository.save(user3);
        Long otherUserId = otherUser.getId();
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(ownerOfItem.getId()))
                .bodyValue(itemDto1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        Long itemId = itemDtoOut.getId();
        setBookings(itemId, userWhoBooked);
        setComments(itemId, userWhoBooked);

        /*Запрос владельца, ассерты соответсвующие*/
        ItemDto itemDtoGet = webTestClient.get()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(ownerId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(itemId, itemDtoGet.getId());
        assertEquals(ownerId, itemDtoGet.getOwner());
        assertEquals(itemDto1.getName(), itemDtoGet.getName());
        assertEquals(itemDto1.getDescription(), itemDtoGet.getDescription());
        assertEquals(itemDto1.getAvailable(), itemDtoGet.getAvailable());
        assertNotNull(itemDtoGet.getLastBooking());
        assertNotNull(itemDtoGet.getNextBooking());
        assertEquals(1, itemDtoGet.getComments()
                .size());


        /*Запрос не владельца, ассерты соответсвующие*/
        ItemDto itemDtoGet2 = webTestClient.get()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(otherUserId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(itemId, itemDtoGet2.getId());
        assertEquals(ownerId, itemDtoGet2.getOwner());
        assertEquals(itemDto1.getName(), itemDtoGet2.getName());
        assertEquals(itemDto1.getDescription(), itemDtoGet2.getDescription());
        assertEquals(itemDto1.getAvailable(), itemDtoGet2.getAvailable());
        assertNull(itemDtoGet2.getLastBooking(), "Последняя бронь не должна быть у не владельца");
        assertNull(itemDtoGet2.getNextBooking(), "Следующая бронь не должна быть у не владельца");
        assertEquals(1, itemDtoGet2.getComments()
                .size());
    }

    /**
     * Метод берет предмет из БД, добавляет брони и комменты, сохраняет в БД.
     *
     * @param itemId id предмета
     * @param booker юзер, который бронировал
     */
    private void setBookings(Long itemId,
                             User booker) {
        Item itemFromD = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Item with id %d not found", itemId)));
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now()
                        .minusDays(3))
                .end(LocalDateTime.now()
                        .minusDays(1))
                .item(itemFromD)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now()
                        .plusDays(1))
                .end(LocalDateTime.now()
                        .plusDays(2))
                .item(itemFromD)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);
    }

    private void setComments(Long itemId,
                             User booker) {
        Item itemFromD = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Item with id %d not found", itemId)));
        Comment firstComment = Comment.builder()
                .item(itemFromD)
                .author(booker)
                .text("Первый комментарий")
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(firstComment);
    }

    @Test
    void addComment_WhenBookedBefore_ReturnCommentDtoOut() {
        User ownerOfItem = userRepository.save(user1);
        Long ownerId = ownerOfItem.getId();
        User userWhoBooked = userRepository.save(user2);
        Long userWhoBookedId = userWhoBooked.getId();
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(ownerOfItem.getId()))
                .bodyValue(itemDto1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        setBookings(itemDtoOut.getId(), userWhoBooked);

        CommentDtoIn commentDtoIn = commentDtoIn1;
        CommentDtoIn commentDtoOut = webTestClient.post()
                .uri("/items/{itemId}/comment", itemDtoOut.getId())
                .header("X-Sharer-User-Id", String.valueOf(userWhoBookedId))
                .bodyValue(commentDtoIn)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentDtoIn.class)
                .returnResult()
                .getResponseBody();
        assertEquals(commentDtoIn.getText(), commentDtoOut.getText());
    }

    @Test
    void addComment_WhenNotBookedBefore_ReturnBadRequest() {
        User ownerOfItem = userRepository.save(user1);
        Long ownerId = ownerOfItem.getId();
        User userWhoNotBooked = userRepository.save(user2);
        Long userWhoNotBookedId = userWhoNotBooked.getId();
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(ownerOfItem.getId()))
                .bodyValue(itemDto1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();

        CommentDtoIn commentDtoIn = commentDtoIn1;
        webTestClient.post()
                .uri("/items/{itemId}/comment", itemDtoOut.getId())
                .header("X-Sharer-User-Id", String.valueOf(userWhoNotBookedId))
                .bodyValue(commentDtoIn)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void getAllOwnerItemsByOwnerId() {
        User ownerOfItem = userRepository.save(user1);
        Long ownerId = ownerOfItem.getId();

        ItemDto firstItem = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(ownerOfItem.getId()))
                .bodyValue(itemDto1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        ItemDto secondItem = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(ownerOfItem.getId()))
                .bodyValue(itemDto2)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();

        List<ItemDto> items = webTestClient.get()
                .uri("/items?from=0&size=10")
                .header("X-Sharer-User-Id", String.valueOf(ownerId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(2, items.size());
    }

    @Test
    void searchItem() {
        User ownerOfItem = userRepository.save(user1);
        Long ownerId = ownerOfItem.getId();

        ItemDto firstItem = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(ownerOfItem.getId()))
                .bodyValue(itemDto1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        ItemDto secondItem = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(ownerOfItem.getId()))
                .bodyValue(itemDto2)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();

        List<ItemDto> items = webTestClient.get()
                .uri("/items/search?text=Клей&from=0&size=10")
                .header("X-Sharer-User-Id", String.valueOf(ownerId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(1, items.size());
    }
}