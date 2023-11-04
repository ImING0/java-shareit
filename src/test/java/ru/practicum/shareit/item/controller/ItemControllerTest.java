package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    private User user1;
    private User user2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name("Alex")
                .email("alex@mail.com").build();
        user2 = User.builder()
                .name("Brain")
                .email("brainDit@gmail.com").build();
        itemDto1 = ItemDto.builder()
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();
        itemDto2 = ItemDto.builder().name("Насвай")
                .description("Хорошая замена клею")
                .available(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createItem_WhenValid_ReturnItemDto() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = User.builder()
                .name("Alex")
                .email("alex@mail.com").build();
        userRepository.save(user);
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(itemId, itemDtoOut.getId());
        assertEquals(userId, itemDtoOut.getOwner());
        assertEquals(itemDto.getName(), itemDtoOut.getName());
        assertEquals(itemDto.getDescription(), itemDtoOut.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtoOut.getAvailable());

    }

    @Test
    void create_WhenInvalidData_ReturnBadRequest() {
        Long userId = 1L;
        userRepository.save(user1);
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
        Long userId = 1L;
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
        Long userId = 1L;
        Long itemId = 1L;
        userRepository.save(user1);
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(itemId, itemDtoOut.getId());

        ItemDto itemToUpdate = itemDto2;
        ItemDto itemDtoUpdated = webTestClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemToUpdate)
                .exchange()
                .expectStatus().isOk()
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
        Long userId = 1L;
        Long itemId = 1L;
        userRepository.save(user1);
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(itemId, itemDtoOut.getId());

        ItemDto itemToUpdate = itemDto2;
        itemToUpdate.setName(null);
        itemToUpdate.setDescription(null);
        itemToUpdate.setAvailable(null);
        webTestClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemToUpdate)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void update_WhenUserNotFound_ReturnNotFound() {
        /*Long userId = 1L;
        Long incorrectUserId = 2L;
        Long itemId = 1L;
        ItemDto itemDto = itemDto1;
        ItemDto itemDtoOut = webTestClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .returnResult()
                .getResponseBody();
        assertEquals(itemId, itemDtoOut.getId());

        ItemDto itemToUpdate = itemDto2;
        webTestClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemToUpdate)
                .exchange()
                .expectStatus().isNotFound();*/
    }

    @Test
    void getItemById() {
    }

    @Test
    void addComment() {
    }

    @Test
    void getAllOwnerItemsByOwnerId() {
    }

    @Test
    void searchItem() {
    }


}