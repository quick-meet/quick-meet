package ru.imo.quickmeet.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.imo.quickmeet.database.entity.Meeting;
import ru.imo.quickmeet.database.entity.User;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, String> {

    // это костыль, если что вызывай через listOf(user), ассоциативную таблицу пилить не хочется
    // костыль норм тема для хакатона
    List<Meeting> findAllByUsers(List<User> strings);
}
