package com.name.brief.web.controller;

import com.name.brief.service.SelfAnalysisService;
import com.name.brief.utils.SelfAnalysisUtils;
import com.name.brief.web.dto.SelfAnalysisDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conference")
public class SelfAnalysisController {
    private final SelfAnalysisService selfAnalysisService;

    public SelfAnalysisController(SelfAnalysisService selfAnalysisService) {
        this.selfAnalysisService = selfAnalysisService;
    }

    @PostMapping("/{conferenceId}/selfAnalysis")
    public SelfAnalysisDto add(@PathVariable Long conferenceId,
                               @RequestBody SelfAnalysisDto dto) {
        return SelfAnalysisDto.createFrom(
                selfAnalysisService.add(conferenceId, dto.createSelfAnalysis()));
    }

    @PutMapping("/{conferenceId}/selfAnalysis")
    public SelfAnalysisDto update(@PathVariable Long conferenceId,
                                  @RequestBody SelfAnalysisDto dto) {
        return SelfAnalysisDto.createFrom(
                selfAnalysisService.update(conferenceId, dto.createSelfAnalysis()));
    }

    @GetMapping("/{conferenceId}/selfAnalysis/participant/{participantId}")
    public SelfAnalysisDto getForParticipant(@PathVariable Long conferenceId,
                                             @PathVariable Long participantId) {
        return SelfAnalysisDto.createFrom(
                selfAnalysisService.getFor(participantId));
    }

    @GetMapping("/{conferenceId}/selfAnalysis/participant/{participantId}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long conferenceId,
                                         @PathVariable Long participantId) {
        byte[] content = SelfAnalysisUtils.createPdf(selfAnalysisService.getFor(participantId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
