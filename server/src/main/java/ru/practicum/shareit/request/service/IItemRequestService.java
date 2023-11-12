package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface IItemRequestService {

    /**
     * Создание заявки на вещь.
     *
     * @param itemRequestDtoIn описание заявки
     * @param userId           идентификатор пользователя
     * @return ItemRequestDtoOut заявка
     */
    ItemRequestDtoOut create(ItemRequestDtoIn itemRequestDtoIn,
                             Long userId);

    /**
     * Получить данные об одном конкретном запросе по его id вместе с данными об ответах на него
     * в том же формате, что и в эндпоинте.
     * Посмотреть данные об отдельном запросе может любой пользователь.
     *
     * @param id     идентификатор заявки
     * @param userId идентификатор пользователя
     * @return ItemRequestDtoOut заявка
     */
    ItemRequestDtoOut getById(Long id,
                              Long userId);

    /**
     * Получить список своих запросов вместе с данными об ответах на них.
     * Запросы сортируются по дате создания: от более новых к более старым.
     *
     * @param userId идентификатор пользователя
     * @return List<ItemRequestDtoOut> список заявок
     */
    List<ItemRequestDtoOut> getAllByUserId(Long userId);

    /**
     * Получить список запросов других пользователей вместе с данными об ответах на них.
     * Запросы сортируются по дате создания: от более новых к более старым.
     *
     * @param userId идентификатор пользователя, нужен, чтобы исключить его заявки из списка
     * @param from   с какой позиции начинать
     * @param size   сколько элементов возвращать
     * @return List<ItemRequestDtoOut> список заявок
     */
    List<ItemRequestDtoOut> getAllFromOthers(Long userId,
                                             Integer from,
                                             Integer size);
}
