package ru.imo.quickmeet;

import lombok.NoArgsConstructor;
import ru.imo.quickmeet.database.entity.UnavailableTimeSlot;
import ru.imo.quickmeet.dto.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@NoArgsConstructor
public final class TestHelpers {
    private static final DateTimeFormatter RU_TIME = DateTimeFormatter.ofPattern("HH:mm");

    public static TimeSlot todaySlot(String ruStartAt, String ruEndAt) {
        var startAt = today(ruStartAt);
        var endAt = today(ruEndAt);
        return new TimeSlot(startAt, endAt);
    }

    public static UnavailableTimeSlot todayUnavailableSlot(String ruStartAt, String ruEndAt) {
        var startAt = today(ruStartAt);
        var endAt = today(ruEndAt);
        var slot = new UnavailableTimeSlot();
        slot.setStartAt(startAt);
        slot.setEndAt(endAt);
        return slot;
    }

    public static LocalDateTime today(String ruStartAt) {
        var today = LocalDate.now();
        return LocalTime.parse(ruStartAt, RU_TIME)
                .atDate(today);
    }
}
