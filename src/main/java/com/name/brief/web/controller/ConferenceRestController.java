package com.name.brief.web.controller;

import com.name.brief.service.BestPracticeService;
import com.name.brief.web.dto.BestPracticeDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conference")
public class ConferenceRestController {
    private final BestPracticeService bestPracticeService;

    public ConferenceRestController(BestPracticeService bestPracticeService) {
        this.bestPracticeService = bestPracticeService;
    }

    @GetMapping("/bestPractice/participant/{participantId}")
    public List<BestPracticeDto> geParticipantBestPractices(@PathVariable Long participantId) {
        return BestPracticeDto.createListFrom(bestPracticeService.getBestPracticesFor(participantId));
    }

    @PostMapping("{gameId}/bestPractice")
    public BestPracticeDto addBestPractice(@PathVariable Long gameId,
                                           @RequestBody BestPracticeDto bestPracticeDto) {
        return BestPracticeDto.createFrom(
                bestPracticeService.add(gameId, bestPracticeDto.createBestPractice()));
    }

    @PostMapping("{gameId}/bestPractice/{bestPracticeId}")
    public BestPracticeDto changeBestPractice(@PathVariable Long gameId,
                                              @PathVariable Long bestPracticeId,
                                              @RequestBody BestPracticeDto dto) {
        return BestPracticeDto.createFrom(bestPracticeService.changeText(bestPracticeId, dto.getText(), gameId));
    }

    @GetMapping("/bestPractice/{gameId}")
    public List<BestPracticeDto> getAllBestPractices(@PathVariable Long gameId) {
        return BestPracticeDto.createListFrom(
                bestPracticeService.getAllBestPractices(gameId));
    }

    @DeleteMapping("{gameId}/bestPractice/{bestPracticeId}")
    public void deleteBestPractice(@PathVariable Long gameId,
                                   @PathVariable Long bestPracticeId) {
        bestPracticeService.delete(gameId, bestPracticeId);
    }
}
