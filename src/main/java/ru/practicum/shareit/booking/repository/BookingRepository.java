package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Покажет, брал ли пользователь эту вещь в аренду и завершилась ли она
     *
     * @param userId          id пользователя
     * @param itemId          id вещи
     * @param currentDateTime текущее время
     * @return бронирование либо пустой Optional
     */
    Optional<Booking> findFirstByBookerIdAndItemIdAndEndIsBefore(Long userId,
                                                                 Long itemId,
                                                                 LocalDateTime currentDateTime);

    /**
     * Получить все бронирования пользователя по id - ALL
     *
     * @param userId id пользователя
     * @return список бронирований
     */
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId,
                                                    Pageable pageable);

    /**
     * Получить список бронирований для всех вещей текущего пользователя по id
     * - ALL
     *
     * @param userId id пользователя
     * @return список бронирований
     */
    List<Booking> findAllByItemOwnerOrderByStartDesc(Long userId,
                                                     Pageable pageable);

    /**
     * Получить все бронирования пользователя по id на текущий момент - CURRENT
     *
     * @param userId      id пользователя
     * @param currentTime текущее время
     * @return список бронирований
     */
    @Query("SELECT b  FROM Booking b" + " JOIN fetch b.item i" + " join fetch b.booker bkr"
            + " where bkr.id = ?1 and ?2 BETWEEN b.start and b.end " + "order by b.start desc ")
    List<Booking> findAllCurrentBookingsByBookerId(Long userId,
                                                   LocalDateTime currentTime,
                                                   Pageable pageable);

    /**
     * Получить список бронирований для всех вещей текущего пользователя по id
     * на текущий момент - CURRENT
     *
     * @param userId
     * @param currentTime
     * @return
     */
    @Query("SELECT b  FROM Booking b" + " JOIN fetch b.item i" + " join fetch b.booker bkr"
            + " where i.owner = ?1 and ?2 BETWEEN b.start and b.end " + "order by b.start desc ")
    List<Booking> findAllCurrentBookingsByItemOwner(Long userId,
                                                    LocalDateTime currentTime,
                                                    Pageable pageable);

    /**
     * Получить все бронирования пользователя по id (завершенные) - PAST
     *
     * @param userId          id пользователя
     * @param currentDateTime текущее время
     * @return список бронирований
     */
    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId,
                                                                  LocalDateTime currentDateTime,
                                                                  Pageable pageable);

    /**
     * Получить список бронирований для всех вещей текущего пользователя по id
     * на прошедший момент - PAST
     *
     * @param userId          id пользователя
     * @param currentDateTime текущее время
     * @return список бронирований
     */
    List<Booking> findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(Long userId,
                                                                   LocalDateTime currentDateTime,
                                                                   Pageable pageable);

    /**
     * Получить все бронирования пользователя по id будущие - FUTURE
     *
     * @param userId          id пользователя
     * @param currentDateTime текущее время
     * @return список бронирований
     */
    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId,
                                                                   LocalDateTime currentDateTime,
                                                                   Pageable pageable);

    /**
     * Получить список бронирований для всех вещей текущего пользователя по id
     * на будущий момент - FUTURE
     *
     * @param userId          id пользователя
     * @param currentDateTime текущее время
     * @return список бронирований
     */
    List<Booking> findAllByItemOwnerAndStartIsAfterOrderByStartDesc(Long userId,
                                                                    LocalDateTime currentDateTime,
                                                                    Pageable pageable);

    /**
     * Получить все бронирования пользователя по id по статусу
     *
     * @param userId id пользователя
     * @param status статус
     * @return список бронирований
     */
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId,
                                                             Status status,
                                                             Pageable pageable);

    /**
     * Получить список бронирований для всех вещей текущего пользователя по id
     * по статусу
     *
     * @param userId id пользователя
     * @param status статус
     * @return список бронирований
     */
    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(Long userId,
                                                              Status status,
                                                              Pageable pageable);

    /**
     * Последнее бронирование вещи по id
     *
     * @param userId          id пользователя
     * @param itemId          id вещи
     * @param currentDateTime текущее время
     * @param status          статус
     * @return бронирование либо пустой Optional
     */
    Optional<Booking> findFirstByItemOwnerAndItemIdAndStartIsBeforeAndStatusOrderByStartDesc(Long userId,
                                                                                             Long itemId,
                                                                                             LocalDateTime currentDateTime,
                                                                                             Status status);

    /**
     * Следующее подтвержденное бронирование вещи по id
     *
     * @param userId          id пользователя
     * @param itemId          id вещи
     * @param currentDateTime текущее время
     * @param status          статус
     * @return бронирование либо пустой Optional
     */
    Optional<Booking> findFirstByItemOwnerAndItemIdAndStartIsAfterAndStatusOrderByStartAsc(Long userId,
                                                                                           Long itemId,
                                                                                           LocalDateTime currentDateTime,
                                                                                           Status status);
}
