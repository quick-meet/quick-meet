package ru.imo.quickmeet.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.imo.quickmeet.database.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
