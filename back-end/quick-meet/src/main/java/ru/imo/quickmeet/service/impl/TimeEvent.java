package ru.imo.quickmeet.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
class TimeEvent  implements Comparable<TimeEvent> {
    LocalDateTime time;
    boolean isStart;

    @Override
    public int compareTo(TimeEvent o) {
        return this.time.compareTo(o.time);
    }
}
