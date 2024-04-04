package ru.imo.quickmeet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.imo.quickmeet.database.entity.Meeting;
import ru.imo.quickmeet.database.entity.User;
import ru.imo.quickmeet.database.repository.MeetingRepository;
import ru.imo.quickmeet.database.repository.UnavailableTimeSlotsRepository;
import ru.imo.quickmeet.database.repository.UserRepository;
import ru.imo.quickmeet.dto.CreatedMeetDTO;
import ru.imo.quickmeet.dto.MeetsDTO;
import ru.imo.quickmeet.dto.NewMeetDTO;
import ru.imo.quickmeet.dto.ReturnedMeetDTO;
import ru.imo.quickmeet.dto.UserRegisterDTO;
import ru.imo.quickmeet.service.MeetingTimeCalculator;
import ru.imo.quickmeet.service.impl.SimpleMeetingTimeCalculator;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/v0/api")
public class MeetController {

    private final MeetingTimeCalculator timeCalculator;
    private final MeetingRepository meetingRepository;
    private final UnavailableTimeSlotsRepository unavailableTimeSlotsRepository;
    private final UserRepository userRepository;

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
        //TODO
        // go to User service and save new user
        //
        User u = new User();
        u.setUserId(user.user_id());
        u.setUserName(user.username());

        User existed = userRepository.getReferenceById(user.user_id());

        if (existed == null) {
            userRepository.saveAndFlush(u);
        }


        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @PostMapping("meet")
    public ReturnedMeetDTO createMeet(@RequestBody NewMeetDTO meet) {
        //TODO
        // go to User service and save new user
        //
        return new ReturnedMeetDTO(0, List.of("@1", "@2", "@123"),
                1000, 2, "template meet");
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

            CreatedMeetDTO createdM = new CreatedMeetDTO(m.getId(),
                    users_nicks, users_ids, m.getTimeStart(), m.getDurationInMinutes(), "");

            meetDTOS.add(createdM);
        }

        return new MeetsDTO(meetDTOS);
    }

    @CrossOrigin
    @PostMapping("user/{tgUserId}")
    public ResponseEntity<String> setBusyTime(@PathVariable String tgUserId, RequestEntity<String> request) {
        //TODO
        // add busy time to data base
        //
        return ResponseEntity.ok().build();
    }
}
