package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.IItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final IItemRequestService itemRequestService;

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
        itemRequestDtoIn.setRequestorId(userId);
        log.info("Creating request for user {}, request {}", userId, itemRequestDtoIn);
        return ResponseEntity.ok(itemRequestService.create(itemRequestDtoIn, userId));
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
        return ResponseEntity.ok(itemRequestService.getById(requestId, userId));
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
        return ResponseEntity.ok(itemRequestService.getAllByUserId(userId));
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
        return ResponseEntity.ok(itemRequestService.getAllFromOthers(userId, from, size));
    }
}
