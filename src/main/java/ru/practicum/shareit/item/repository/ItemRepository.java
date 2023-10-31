package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Получить все вещи пользователя по id владельца
     *
     * @param ownerId id владельца
     * @return список вещей
     */
    List<Item> findAllByOwner(Long ownerId,
                              Pageable pageable);

    /**
     * Поиск вещей по тексту
     *
     * @param text текст для поиска
     * @return список вещей, удовлетворяющих условию поиска
     */
    @Query("SELECT i " + "from Item as i " + "where i.available = true and "
            + "(lower(i.name) like lower(concat('%', ?1, '%') ) or "
            + "lower(i.description) like lower(concat('%', ?1, '%') ))")
    List<Item> search(String text,
                      Pageable pageable);
}
