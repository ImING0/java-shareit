package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Получение списка заявок пользователя.
     * Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     *
     * @param userId идентификатор пользователя
     * @return список заявок пользователя
     */
    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(Long userId);

    /**
     * Получение списка заявок на вещи других пользователей,
     * которые не принадлежат пользователю.
     * Режим сортировки задается в параметре Pageable
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры пагинации
     * @return список заявок
     */
    Page<ItemRequest> findAllByRequestorNot(Long userId,
                                            Pageable pageable);
}
