package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@Entity
@Table(name = "bookings", schema = "PUBLIC")
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @Column(name = "status", nullable = false)
    /* , columnDefinition = "ENUM DEFAULT 'WAITING'"*/
    /*Тыкался я мыкался, хотел сделать, чтобы отсюда сразу шла вставка по дефолту
     * убил кучу времени но ничего не получилось и пришлось уже в сервисе передавать значение
     * может подскажете способ?*/
    @Enumerated(EnumType.STRING)
    private Status status;
}
