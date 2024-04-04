package ru.imo.quickmeet.service;

import ru.imo.quickmeet.database.entity.UnavailableTimeSlot;
import ru.imo.quickmeet.dto.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface MeetingTimeCalculator {

    /**
     * Calculate meeting time for participants.
     *
     * @param meetingTimeMinutes   meeting time in minutes
     * @param leftBound            left bound of meeting time
     * @param rightBound           right bound of meeting time
     * @param unavailableTimeSlots list of unavailable time slots
     * @return found time slot
     */
    Optional<TimeSlot> calculate(
            long meetingTimeMinutes,
            LocalDateTime leftBound,
            LocalDateTime rightBound,
            List<UnavailableTimeSlot> unavailableTimeSlots
    );

}
