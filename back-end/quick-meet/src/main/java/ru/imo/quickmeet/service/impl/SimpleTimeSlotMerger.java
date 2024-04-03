package ru.imo.quickmeet.service.impl;

import ru.imo.quickmeet.dto.TimeSlot;
import ru.imo.quickmeet.service.TimeSlotMerger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class SimpleTimeSlotMerger implements TimeSlotMerger {
    @Override
    public List<TimeSlot> merge(List<TimeSlot> slots) {
        if (slots.size() <= 1) {
            return slots;
        }

        var mutableSlots = new ArrayList<>(slots);

        Collections.sort(mutableSlots);

        var mergedSlots = new ArrayList<TimeSlot>();
        var current = mutableSlots.get(0);

        for (int i = 1; i < mutableSlots.size(); i++) {
            var next = mutableSlots.get(i);

            if (!current.getEndAt().isBefore(next.getStartAt())) {
                current = new TimeSlot(current.getStartAt(), max(current.getEndAt(), next.getEndAt()));
            } else {
                mergedSlots.add(current);
                current = next;
            }
        }

        mergedSlots.add(current);

        return mergedSlots;
    }

    private static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }
}
