package ru.imo.quickmeet.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.imo.quickmeet.database.entity.UnavailableTimeSlot;
import ru.imo.quickmeet.dto.TimeSlot;
import ru.imo.quickmeet.service.MeetingTimeCalculator;
import ru.imo.quickmeet.service.TimeSlotMerger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Service
public class SimpleMeetingTimeCalculator implements MeetingTimeCalculator {
    private final TimeSlotMerger timeSlotMerger;

    @Autowired
    public SimpleMeetingTimeCalculator(TimeSlotMerger timeSlotMerger) {
        this.timeSlotMerger = timeSlotMerger;
    }

    @Override
    public Optional<TimeSlot> calculate(
            long meetingTimeMinutes,
            LocalDateTime leftBound,
            LocalDateTime rightBound,
            List<UnavailableTimeSlot> unavailableTimeSlots) {

        if (unavailableTimeSlots.isEmpty()) {
            return Optional.of(new TimeSlot(leftBound, leftBound.plusMinutes(meetingTimeMinutes)));
        }

        var timeSlots = timeSlotMerger.merge(
                unavailableTimeSlots.stream()
                        .map(it -> new TimeSlot(it.getStartAt(), it.getEndAt()))
                        .toList()
        );

        if(timeSlots.isEmpty()) {
            return Optional.empty();
        }

        for (var timeSlot : timeSlots) {
            var freeTime = Duration.between(leftBound, timeSlot.getStartAt());
            if(freeTime.toMinutes() >= meetingTimeMinutes) {
                var endAt = leftBound.plusMinutes(meetingTimeMinutes);
                if (!endAt.isAfter(rightBound)) {
                    return Optional.of(new TimeSlot(leftBound, endAt));
                }
            }
            leftBound = timeSlot.getEndAt();
        }

        var endAt = leftBound.plusMinutes(meetingTimeMinutes);
        if(!endAt.isAfter(rightBound)) {
            return Optional.of(new TimeSlot(leftBound, endAt));
        }

        return Optional.empty();
    }

}
