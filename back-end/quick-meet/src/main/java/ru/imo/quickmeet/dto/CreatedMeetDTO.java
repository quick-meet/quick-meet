package ru.imo.quickmeet.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CreatedMeetDTO(long meet_id, List<String> users_nicks,
                             List<String> users_ids,
                             LocalDateTime time_start, long duration,
                             String comment) {
}
