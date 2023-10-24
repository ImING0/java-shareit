package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Data
@Builder
@Table(name = "users", schema = "PUBLIC")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // Мне кажется, что проверки на нулль тут избыточны, ведь в БД уже есть проверка на это?
    // или это обеспечивает большую безопастность ?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
}
