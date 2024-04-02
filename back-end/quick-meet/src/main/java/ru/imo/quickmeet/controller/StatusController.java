package ru.imo.quickmeet.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @CrossOrigin
    @GetMapping("v0/api/status")
    public String getUser() {
        return "{\"success\":1}";
    }

}
