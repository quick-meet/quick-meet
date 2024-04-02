package ru.imo.quickmeet.service;

import ru.imo.quickmeet.dto.TimeSlot;

import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface TimeSlotMerger {

    /**
     * Merge intersected time slots.
     *
     * @param slots time slots to merge
     * @return merged time slots
     */
    List<TimeSlot> merge(List<TimeSlot> slots);

}
