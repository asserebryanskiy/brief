package com.name.brief.web.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DoctorAnswerDto {
    private String comment;
    private Map<String, String> expertiseEstimations = new HashMap<>();

    public DoctorAnswerDto() {
    }
}
