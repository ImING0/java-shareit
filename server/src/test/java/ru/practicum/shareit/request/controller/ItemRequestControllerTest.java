package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ItemRequestControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User requestor1;
    private User requestor2;
    private User ownerOfItems;
    private User userWithNoRequestsAndItems;
    private ItemRequestDtoIn firstItemRequestDtoIn;

    @BeforeEach
    void setUp() {
        requestor1 = User.builder()
                .name("Alex")
                .email("alex@mail.com")
                .build();
        requestor2 = User.builder()
                .name("Brain")
                .email("brainDit@gmail.com")
                .build();
        ownerOfItems = User.builder()
                .name("Ержан")
                .email("vstavaierjan@mail.ru")
                .build();
        userWithNoRequestsAndItems = User.builder()
                .name("NoRequestsAndItems")
                .email("somemail@mail.ru")
                .build();
        firstItemRequestDtoIn = ItemRequestDtoIn.builder()
                .description("Мне нужен котик на выходные")
                .build();
    }

    @Test
    void create_WhenUserNotFound_ReturnNotFount() {
        Long wrongUserId = 100L;
        webTestClient.post()
                .uri("/requests")
                .header("X-Sharer-User-Id", String.valueOf(wrongUserId))
                .bodyValue(firstItemRequestDtoIn)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void create_WhenUserFound_ReturnOk() {
        User requestor = userRepository.save(requestor1);
        firstItemRequestDtoIn.setRequestorId(requestor.getId());
        ItemRequestDtoOut itemRequestDtoOut = webTestClient.post()
                .uri("/requests")
                .header("X-Sharer-User-Id", String.valueOf(requestor.getId()))
                .bodyValue(firstItemRequestDtoIn)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemRequestDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(firstItemRequestDtoIn.getDescription(), itemRequestDtoOut.getDescription());
        assertEquals(requestor.getId(), itemRequestDtoOut.getRequestor());
    }

    @Test
    void getById_WhenUserNotFound_ReturnNotFound() {
        Long wrongUserId = 100L;
        webTestClient.get()
                .uri("/requests/" + wrongUserId)
                .header("X-Sharer-User-Id", String.valueOf(wrongUserId))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getById_WhenRequestNotFound_ReturnNotFound() {
        User requestor = userRepository.save(requestor1);
        Long wrongRequestId = 100L;
        webTestClient.get()
                .uri("/requests/" + wrongRequestId)
                .header("X-Sharer-User-Id", String.valueOf(requestor.getId()))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getById_WhenUserAndRequestFoundWithOutItems_ReturnItemRequestDtoOut() {
        User requestor = userRepository.save(requestor1);
        ItemRequest itemRequest = ItemRequest.builder()
                .description("Мне нужен котик на выходные")
                .requestor(requestor.getId())
                .created(LocalDateTime.now()
                        .minusHours(2))
                .build();
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemRequestDtoOut itemRequestDtoOut = webTestClient.get()
                .uri("/requests/" + itemRequest.getId())
                .header("X-Sharer-User-Id", String.valueOf(requestor.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemRequestDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(firstItemRequestDtoIn.getDescription(), itemRequestDtoOut.getDescription());
        assertEquals(requestor.getId(), itemRequestDtoOut.getRequestor());
        assertEquals(0, itemRequestDtoOut.getItems()
                .size());
    }

    @Test
    void getById_WhenUserAndRequestFoundWithItems_ReturnItemRequestDtoOut() {
        User firstRequestorWithOneRequest = userRepository.save(requestor1);
        User secondRequestorWithTwoRequest = userRepository.save(requestor2);
        User ownerOfAllItems = userRepository.save(ownerOfItems);
        User otherUser = userRepository.save(userWithNoRequestsAndItems);
        Long requestId = setRequestorOwnersAndItems(firstRequestorWithOneRequest,
                secondRequestorWithTwoRequest, ownerOfAllItems);

        ItemRequestDtoOut itemRequestDtoOut = webTestClient.get()
                .uri("/requests/" + requestId)
                .header("X-Sharer-User-Id", String.valueOf(otherUser.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ItemRequestDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(2, itemRequestDtoOut.getItems()
                .size());
    }

    @Test
    void getAllByUserId_WhenTwoRequests_ReturnTwoRequestDtoOut() {
        User firstRequestorWithOneRequest = userRepository.save(requestor1);
        User secondRequestorWithTwoRequest = userRepository.save(requestor2);
        User ownerOfAllItems = userRepository.save(ownerOfItems);
        setRequestorOwnersAndItems(firstRequestorWithOneRequest, secondRequestorWithTwoRequest,
                ownerOfAllItems);

        List<ItemRequestDtoOut> itemRequestDtoOuts = webTestClient.get()
                .uri("/requests")
                .header("X-Sharer-User-Id", String.valueOf(secondRequestorWithTwoRequest.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ItemRequestDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(2, itemRequestDtoOuts.size());
    }

    @Test
    void getAllRequests_WhenThreeRequests_ReturnThreeRequestDtoOut() {
        User firstRequestorWithOneRequest = userRepository.save(requestor1);
        User secondRequestorWithTwoRequest = userRepository.save(requestor2);
        User ownerOfAllItems = userRepository.save(ownerOfItems);
        User otherUser = userRepository.save(userWithNoRequestsAndItems);
        setRequestorOwnersAndItems(firstRequestorWithOneRequest, secondRequestorWithTwoRequest,
                ownerOfAllItems);

        List<ItemRequestDtoOut> itemRequestDtoOuts = webTestClient.get()
                .uri("/requests/all")
                .header("X-Sharer-User-Id", String.valueOf(otherUser.getId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ItemRequestDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertEquals(3, itemRequestDtoOuts.size());
    }

    private Long setRequestorOwnersAndItems(User firstRequestorWithOneRequest,
                                            User secondRequestorWithTwoRequest,
                                            User ownerOfAllItems) {
        /*У нас будет два реквестера и один владелец.
         * У первого реквестора будет 2 запроса, у второго 1
         * Владелец предложит вещи им всем.
         * Только на запрос котика будет два ответа */
        ItemRequest firstRequestOfFirstRequestor = ItemRequest.builder()
                .description("Мне нужен котик на выходные")
                .requestor(firstRequestorWithOneRequest.getId())
                .created(LocalDateTime.now()
                        .minusHours(2))
                .build();
        firstRequestOfFirstRequestor = itemRequestRepository.save(firstRequestOfFirstRequestor);

        ItemRequest firstRequestOfSecondRequestor = ItemRequest.builder()
                .requestor(secondRequestorWithTwoRequest.getId())
                .description("Мне нужно мыло")
                .created(LocalDateTime.now()
                        .minusHours(1))
                .build();
        firstRequestOfSecondRequestor = itemRequestRepository.save(firstRequestOfSecondRequestor);

        ItemRequest secondRequestOfSecondRequestor = ItemRequest.builder()
                .description("Мне нужна веревка")
                .requestor(secondRequestorWithTwoRequest.getId())
                .created(LocalDateTime.now()
                        .minusHours(2))
                .build();
        secondRequestOfSecondRequestor = itemRequestRepository.save(secondRequestOfSecondRequestor);

        /*Создадим все предметы в ответ на запросы*/

        Item pussy = Item.builder()
                .owner(ownerOfAllItems.getId())
                .name("Кот васька")
                .description("Жрет как не в себя")
                .available(Boolean.TRUE)
                .request(firstRequestOfFirstRequestor.getId())
                .build();
        pussy = itemRepository.save(pussy);

        Item kitty = Item.builder()
                .owner(ownerOfAllItems.getId())
                .name("Кот мурзик")
                .description("Любит спать")
                .available(Boolean.TRUE)
                .request(firstRequestOfFirstRequestor.getId())
                .build();
        kitty = itemRepository.save(kitty);

        Item soap = Item.builder()
                .owner(ownerOfAllItems.getId())
                .name("Мыло")
                .description("Вместе с веревкой создаст переломный момент")
                .available(Boolean.TRUE)
                .request(firstRequestOfSecondRequestor.getId())
                .build();
        soap = itemRepository.save(soap);

        Item rope = Item.builder()
                .owner(ownerOfAllItems.getId())
                .name("Веревка")
                .description("Выдержит вес человека")
                .available(Boolean.TRUE)
                .request(secondRequestOfSecondRequestor.getId())
                .build();
        rope = itemRepository.save(rope);

        return firstRequestOfFirstRequestor.getId();
    }
}