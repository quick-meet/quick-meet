package ru.imo.quickmeet.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.imo.quickmeet.database.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

}
