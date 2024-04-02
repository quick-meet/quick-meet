package ru.imo.quickmeet.service.impl;

import ru.imo.quickmeet.dto.Participant;
import ru.imo.quickmeet.dto.TimeSlot;
import ru.imo.quickmeet.service.MeetingTimeCalculator;
import ru.imo.quickmeet.service.TimeSlotMerger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class SimpleMeetingTimeCalculator implements MeetingTimeCalculator {
    private final TimeSlotMerger timeSlotMerger;

    public SimpleMeetingTimeCalculator(TimeSlotMerger timeSlotMerger) {
        this.timeSlotMerger = timeSlotMerger;
    }

    @Override
    public Optional<TimeSlot> calculate(int meetingTimeMinutes, List<Participant> participants) {
        var events = new ArrayList<TimeEvent>();
        for (var participant : participants) {
            var timeSlots = timeSlotMerger.merge(participant.getTimeSlots());

            for (var slot : timeSlots) {
                if (!slot.hasMinutes(meetingTimeMinutes)) {
                    continue;
                }
                events.add(new TimeEvent(slot.getStartAt(), true));
                events.add(new TimeEvent(slot.getEndAt(), false));
            }
        }

        Collections.sort(events);

        int count = 0;
        LocalDateTime potentialStart = null;
        for (var event : events) {
            if (event.isStart) {
                count++;
                if (count == participants.size()) {
                    potentialStart = event.time;
                }
                continue;
            }
            if (potentialStart != null && count == participants.size()) {
                var potentialEnd = event.time;
                var duration = ChronoUnit.MINUTES.between(potentialStart, potentialEnd);
                 if (duration >= meetingTimeMinutes) {
                    return Optional.of(new TimeSlot(potentialStart, potentialStart.plusMinutes(meetingTimeMinutes)));
                }
            }
            count--;
        }

        return Optional.empty();
    }

}
