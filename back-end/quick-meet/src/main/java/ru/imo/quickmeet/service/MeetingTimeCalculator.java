package ru.imo.quickmeet.service;

import ru.imo.quickmeet.dto.Participant;
import ru.imo.quickmeet.dto.TimeSlot;

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
     * @param meetingTimeMinutes meeting time in minutes
     * @param participants       list of participants
     * @return found time slot or {@link Participant}
     */
    Optional<TimeSlot> calculate(int meetingTimeMinutes, List<Participant> participants);

}
