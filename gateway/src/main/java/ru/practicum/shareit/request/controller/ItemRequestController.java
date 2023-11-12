package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    /**
     * Создание заявки на вещь.
     *
     * @param userId           идентификатор пользователя создающего заявку
     * @param itemRequestDtoIn описание заявки
     * @return ItemRequestDtoOut заявка
     */
    @PostMapping
    public ResponseEntity<ItemRequestDtoOut> create(
            @RequestHeader(name = requestHeader) Long userId,
            @RequestBody @Valid ItemRequestDtoIn itemRequestDtoIn) {
        log.info("Creating request for user {}, request {}", userId, itemRequestDtoIn);
        return ResponseEntity.ok(itemRequestClient.create(itemRequestDtoIn, userId)
                .getBody());
    }

    /**
     * Получить данные об одном конкретном запросе по его id вместе с данными об ответах на него.
     *
     * @param requestId идентификатор заявки
     * @param userId    идентификатор пользователя
     * @return ItemRequestDtoOut заявка
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoOut> getById(
            @PathVariable(name = "requestId") Long requestId,
            @RequestHeader(name = requestHeader) Long userId) {
        return ResponseEntity.ok(itemRequestClient.getById(requestId, userId)
                .getBody());
    }

    /**
     * Получение всех заявок по id их владельца.
     *
     * @param userId идентификатор пользователя
     * @return список заявок
     */
    @GetMapping
    public ResponseEntity<List<ItemRequestDtoOut>> getAllByUserId(
            @RequestHeader(name = requestHeader) Long userId) {
        return ResponseEntity.ok(itemRequestClient.getAllByUserId(userId)
                .getBody());
    }

    /**
     * Получение всех заявок от других пользователей.
     *
     * @param userId идентификатор пользователя
     * @param from   с какой позиции начинать
     * @param size   сколько элементов возвращать
     * @return список заявок
     */
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoOut>> getAllRequests(
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero Integer size,
            @RequestHeader(name = requestHeader) Long userId) {
        return ResponseEntity.ok(itemRequestClient.getAllFromOthers(userId, from, size)
                .getBody());
    }
}
