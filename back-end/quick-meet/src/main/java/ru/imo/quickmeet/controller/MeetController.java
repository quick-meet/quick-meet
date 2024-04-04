package ru.imo.quickmeet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.imo.quickmeet.database.entity.Meeting;
import ru.imo.quickmeet.database.entity.UnavailableTimeSlot;
import ru.imo.quickmeet.database.entity.User;
import ru.imo.quickmeet.database.repository.MeetingRepository;
import ru.imo.quickmeet.database.repository.UnavailableTimeSlotsRepository;
import ru.imo.quickmeet.database.repository.UserRepository;
import ru.imo.quickmeet.dto.*;
import ru.imo.quickmeet.service.MeetingTimeCalculator;
import ru.imo.quickmeet.service.impl.SimpleMeetingTimeCalculator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/v0/api")
public class MeetController {

    private final MeetingTimeCalculator timeCalculator;
    private final MeetingRepository meetingRepository;
    private final UnavailableTimeSlotsRepository unavailableTimeSlotsRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public MeetController(SimpleMeetingTimeCalculator calculator, MeetingRepository meetingRepository, UnavailableTimeSlotsRepository unavailableTimeSlotsRepository, UserRepository userRepository) {
        timeCalculator = calculator;
        this.meetingRepository = meetingRepository;
        this.unavailableTimeSlotsRepository = unavailableTimeSlotsRepository;
        this.userRepository = userRepository;
    }

    @CrossOrigin
    @PostMapping("bot-start")
    public ResponseEntity<String> startBot(@RequestBody UserRegisterDTO user) {
        User u = new User();
        u.setUserId(user.user_id());
        u.setUserName(user.username());


        if (!userRepository.findById(user.user_id()).isPresent()) {
            userRepository.saveAndFlush(u);
        }


        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @PostMapping("meet")
    public CreatedMeetDTO createMeet(@RequestBody NewMeetDTO meet) {
//        var leftBound = Instant.ofEpochMilli(meet.time_start())
//                .atZone(ZoneOffset.UTC)
//                .toLocalDateTime();
//        var rightBound = Instant.ofEpochMilli(meet.time_start())
//                .atZone(ZoneOffset.UTC)
//                .toLocalDateTime();

        LocalDateTime leftBound = LocalDateTime.parse(meet.time_start(), formatter);
        LocalDateTime rightBound = LocalDateTime.parse(meet.time_end(), formatter);

        var users = userRepository.findAllByUserNameIn(meet.users());

        var unavailableTimeSlots = unavailableTimeSlotsRepository.findAllByUserInAndEndAtAfterAndStartAtBefore(
                users,
                leftBound,
                rightBound
        );

        var duration = meet.duration();
        var timeSlotOptional = timeCalculator.calculate(duration, leftBound, rightBound, unavailableTimeSlots);
        if(timeSlotOptional.isEmpty()) {
            return new CreatedMeetDTO(-1, Collections.emptyList(),
                    Collections.emptyList(), "1970-01-01 12:00", 0, "Слот не найден");
        }
        var timeSlot = timeSlotOptional.get();

        var meeting = Meeting.builder()
                .timeStart(timeSlot.getStartAt())
                .timeEnd(timeSlot.getEndAt())
                .durationInMinutes(duration)
                .users(users)
                .build();

        meeting = meetingRepository.save(meeting);

        var timeStart = timeSlot.getStartAt().toEpochSecond(ZoneOffset.UTC);

        var userNames = users.stream()
                .map(User::getUserName)
                .toList();
        List<String> userIds = users.stream()
                .map(User::getUserId)
                .toList();

        String resultTime = timeSlot.getStartAt().format(formatter);
        return new CreatedMeetDTO(meeting.getId(), userNames, userIds, resultTime, duration, "");
    }

    @CrossOrigin
    @GetMapping("meet")
    public MeetsDTO getMeets(@RequestParam String username) {
        User user = userRepository.findByUserName(username);

        List<Meeting> createdMeets =  meetingRepository.findAllByUsers(List.of(user));

        List<CreatedMeetDTO> meetDTOS = new LinkedList<>();

        for (Meeting m : createdMeets) {
            List<String> users_nicks = new LinkedList<>();
            List<String> users_ids = new LinkedList<>();

            for (User u : m.getUsers()) {
                users_nicks.add(u.getUserName());
                users_ids.add(u.getUserId());
            }

            String resultTime = m.getTimeStart().format(formatter);

            CreatedMeetDTO createdM = new CreatedMeetDTO(m.getId(),
                    users_nicks, users_ids, resultTime, m.getDurationInMinutes(), "");

            meetDTOS.add(createdM);
        }

        return new MeetsDTO(meetDTOS);
    }

    @CrossOrigin
    @PostMapping("user/{tgUserName}")
    public ResponseEntity<String> setBusyTime(@PathVariable String tgUserName,
                                              @RequestBody BusyTimeDTO busyTime) {


        LocalDateTime startTime = LocalDateTime.parse(busyTime.start_time(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(busyTime.end_time(), formatter);

        User u = userRepository.findByUserName(tgUserName);
        UnavailableTimeSlot slot = new UnavailableTimeSlot();

        slot.setUser(u);
        slot.setStartAt(startTime);
        slot.setEndAt(endTime);
        unavailableTimeSlotsRepository.saveAndFlush(slot);
        return ResponseEntity.ok().build();
        //return new BusyTimeDTO(LocalDateTime.now(), LocalDateTime.now());
    }
}
