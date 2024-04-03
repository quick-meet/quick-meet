package ru.imo.quickmeet.dto;


import java.util.List;

public record ReturnedMeetDTO(int meet_id, List<String> users, long time_start, long duration, String comment) {
}
