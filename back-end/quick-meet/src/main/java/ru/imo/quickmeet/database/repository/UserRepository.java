package ru.imo.quickmeet.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.imo.quickmeet.database.entity.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, String> {

    User findByUserName(String userName);

    List<User> findAllByUserNameIn(List<String> userNames);

}
