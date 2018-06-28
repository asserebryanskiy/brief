package com.name.brief.web.controller;

import com.name.brief.service.SelfAnalysisService;
import com.name.brief.web.dto.SelfAnalysisDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
                                         @PathVariable Long participantId,
                                         HttpServletResponse response) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get("test.pdf"));
//        byte[] content = Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/page229.pdf"));
//        byte[] content = SelfAnalysisUtils.createPdf(selfAnalysisService.getFor(participantId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set("Content-disposition", "attachment; filename=" + "\"report.pdf\"");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
