package ru.imo.quickmeet.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.imo.quickmeet.database.entity.UnavailableTimeSlot;
import ru.imo.quickmeet.database.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface UnavailableTimeSlotsRepository extends JpaRepository<UnavailableTimeSlot, Long> {
    List<UnavailableTimeSlot> findAllByUserInAndEndAtAfterAndStartAtBefore(
            Set<User> users,
            LocalDateTime startAt,
            LocalDateTime endAt
    );

}
