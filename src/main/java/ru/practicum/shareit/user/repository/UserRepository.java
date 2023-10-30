package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Проверка существования пользователя по email
     *
     * @param email email пользователя
     * @return true, если пользователь существует
     */
    boolean existsByEmail(String email);
}
