package com.name.brief.web.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AverageStatisticsDto {
    private Map<String, String> averageCompetenciesResults = new HashMap<>();
    private Map<String, Integer> averageError = new HashMap<>();
    private Map<String, String> averageErrorCssClass = new HashMap<>();
}
