package ru.imo.quickmeet.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.imo.quickmeet.dto.NewMeetDTO;
import ru.imo.quickmeet.dto.ReturnedMeetDTO;
import ru.imo.quickmeet.dto.UserRegisterDTO;

import java.util.List;

@RestController
@RequestMapping(value = "/v0/api")
public class MeetController {
    @CrossOrigin
    @PostMapping("bot-start")
    public ResponseEntity<String> startBot(@RequestBody UserRegisterDTO user) {
        //TODO
        // go to User service and save new user
        //
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
    @PostMapping("meet/{meetId}")
    public ResponseEntity<String> acceptMeet(@PathVariable int meetId, RequestEntity<String> request) {
        //TODO
        // set meet accepted, or set this meet unavailable,
        // so need to delete meet from all users and send notifications to them
        //
        return ResponseEntity.ok().build();
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
