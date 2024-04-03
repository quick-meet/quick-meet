package ru.imo.quickmeet.dto;


import java.sql.Timestamp;
import java.util.List;

public record NewMeetDTO(List<String> users, long time_start, long time_end, long duration) {
}
