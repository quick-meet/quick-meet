package ru.imo.quickmeet.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.imo.quickmeet.database.entity.UnavailableTimeSlot;

public interface UnavailableTimeSlotsRepository extends JpaRepository<UnavailableTimeSlot, Long> {

}
