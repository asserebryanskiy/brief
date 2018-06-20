package com.name.brief.web.controller;

import com.name.brief.service.GreetingService;
import com.name.brief.web.dto.GreetingDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conference/greeting")
public class GreetingRestController {
    private final GreetingService greetingService;

    public GreetingRestController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("")
    public GreetingDto get(@RequestParam("conferenceId") Long conferenceId,
                           @RequestParam("participantId") Long participantId) {
        return GreetingDto.createFrom(greetingService.getGreetingAnswer(conferenceId, participantId));
    }

    @PostMapping("")
    public Long addAnswer(@RequestBody GreetingDto dto) {
        return greetingService.addAnswer(dto.getConferenceId(), dto.createGreetingAnswer());
    }

    @PutMapping("/{greetingAnswerId}")
    public String updateAnswer(@PathVariable Long greetingAnswerId, @RequestBody GreetingDto dto) {
        greetingService.updateAnswer(greetingAnswerId, dto.getImgIndex(), dto.getComment());
        return "OK";
    }
}
