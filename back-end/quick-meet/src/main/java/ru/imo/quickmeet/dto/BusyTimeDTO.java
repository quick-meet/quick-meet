package ru.imo.quickmeet.dto;

import java.time.LocalDateTime;

public record BusyTimeDTO(LocalDateTime start_time, LocalDateTime end_time) {
}
