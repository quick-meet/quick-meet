package ru.imo.quickmeet.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Getter
@EqualsAndHashCode
public class TimeSlot implements Comparable<TimeSlot> {
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final Duration duration;

    public TimeSlot(LocalDateTime startAt, LocalDateTime endAt) {
        Objects.requireNonNull(startAt, "startAt cannot be null");
        Objects.requireNonNull(endAt, "endAt cannot be null");
        if (startAt.isAfter(endAt)) {
            throw new IllegalArgumentException("startAt must be less than endAt");
        }
        this.startAt = startAt;
        this.endAt = endAt;
        this.duration = Duration.between(startAt, endAt);
    }

    public boolean hasMinutes(int minutes) {
        return duration.getSeconds() / 60 >= minutes;
    }

    @Override
    public int compareTo(TimeSlot other) {
        return Comparator.comparing(TimeSlot::getStartAt)
                .thenComparing(TimeSlot::getEndAt)
                .compare(this, other);
    }
}
