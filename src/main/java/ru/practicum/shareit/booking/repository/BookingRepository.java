package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /*Получить все бронирования пользователя по id ALL*/
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    /*Получить список бронирований для всех вещей текущего пользователя по id ALL*/
    List<Booking> findAllByItemOwnerOrderByStartDesc(Long userId);

    @Query("select b  FROM Booking b" + " JOIN fetch b.item i" + " join fetch b.booker bkr"
            + " where bkr.id = ?1 and ?2 BETWEEN b.start and b.end " + "order by b.start desc ")
        /*Получить все бронирования пользователя по id на текущий момент CURRENT*/
    List<Booking> findAllCurrentBookingsByBookerId(Long userId,
                                                   LocalDateTime currentTime);

    /*Получить список бронирований для всех вещей текущего пользователя по id
     * на текущий момент CURRENT*/
    @Query("select b  FROM Booking b" + " JOIN fetch b.item i" + " join fetch b.booker bkr"
            + " where i.owner = ?1 and ?2 BETWEEN b.start and b.end " + "order by b.start desc ")
    List<Booking> findAllCurrentBookingsByItemOwner(Long userId,
                                                    LocalDateTime currentTime);

    /*Получить все бронирования пользователя по id завершенные PAST*/
    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId,
                                                                  LocalDateTime currentDateTime);

    /*Получить список бронирований для всех вещей текущего пользователя по id
     * на прошедший момент PAST*/
    List<Booking> findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(Long userId,
                                                                   LocalDateTime currentDateTime);

    /*Получить все бронирования пользователя по id будущие FUTURE*/
    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId,
                                                                   LocalDateTime currentDateTime);

    /*Получить список бронирований для всех вещей текущего пользователя по id
     * на будущий момент FUTURE*/
    List<Booking> findAllByItemOwnerAndStartIsAfterOrderByStartDesc(Long userId,
                                                                    LocalDateTime currentDateTime);

    /*Получить все бронирования пользователя по id по статусу*/
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId,
                                                             Status status);

    /*Получить список бронирований для всех вещей текущего пользователя по id
     * по статусу*/
    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(Long userId,
                                                              Status status);

    /*Последнее  бронирование вещи по id*/
    Optional<Booking> findFirstByItemOwnerAndItemIdAndStartIsBeforeAndStatusOrderByStartDesc(Long userId,
                                                                                             Long itemId,
                                                                                             LocalDateTime currentDateTime,
                                                                                             Status status);

    /*Следующее подтвержденное бронирование вещи по id*/
    Optional<Booking> findFirstByItemOwnerAndItemIdAndStartIsAfterAndStatusOrderByStartAsc(Long userId,
                                                                                           Long itemId,
                                                                                           LocalDateTime currentDateTime,
                                                                                           Status status);
}
