package ru.imo.quickmeet.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.imo.quickmeet.database.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

}
