package ru.imo.quickmeet.dto;


import java.time.LocalDateTime;
import java.util.List;

public record NewMeetDTO(List<String> users,
                         String time_start,
                         String time_end, long duration) {
}
