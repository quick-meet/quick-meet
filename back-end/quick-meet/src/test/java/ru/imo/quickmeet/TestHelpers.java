package ru.imo.quickmeet;

import lombok.NoArgsConstructor;
import ru.imo.quickmeet.dto.Participant;
import ru.imo.quickmeet.dto.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@NoArgsConstructor
public final class TestHelpers {
    private static final DateTimeFormatter RU_TIME = DateTimeFormatter.ofPattern("HH:mm");

    public static Participant todayParticipant(String... ruTimeSlots) {
        var timeSlots = new ArrayList<TimeSlot>();
        var today = LocalDate.now();
        for (int i = 0; i < ruTimeSlots.length / 2; i++) {
            var ruStartAt = ruTimeSlots[i * 2];
            var ruEndAt = ruTimeSlots[i * 2 + 1];
            var startAt = LocalTime.parse(ruStartAt, RU_TIME)
                    .atDate(today);
            var endAt = LocalTime.parse(ruEndAt, RU_TIME)
                    .atDate(today);
            timeSlots.add(new TimeSlot(startAt, endAt));
        }
        return new Participant(timeSlots);
    }

    public static TimeSlot todaySlot(String ruStartAt, String ruEndAt) {
        var today = LocalDate.now();
        var startAt = LocalTime.parse(ruStartAt, RU_TIME)
                .atDate(today);
        var endAt = LocalTime.parse(ruEndAt, RU_TIME)
                .atDate(today);
        return new TimeSlot(startAt, endAt);
    }
}
