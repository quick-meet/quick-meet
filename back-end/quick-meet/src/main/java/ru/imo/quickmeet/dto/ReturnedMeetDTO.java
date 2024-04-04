package ru.imo.quickmeet.dto;


import java.util.List;

public record ReturnedMeetDTO(long meet_id, List<String> users, long time_start, long duration, String comment) {
}
