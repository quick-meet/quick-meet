package ru.imo.quickmeet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    private List<TimeSlot> timeSlots;
}
