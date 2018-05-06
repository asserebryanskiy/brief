package com.name.brief.web.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SalesmanStatisticsDto {

    private Map<String, String> competenciesAverage = new HashMap<>();
    private Map<String, List<Integer>> playerAnswersPerRound = new HashMap<>();
    private Map<String, List<String>> correctAnswersPerRound = new HashMap<>();
    private Map<String, List<Integer>> successRatePerRound = new HashMap<>();
    private Map<String, List<String>> successRateCssClassPerRound = new HashMap<>();
    private List<String> comments = new ArrayList<>();

    public SalesmanStatisticsDto() {
    }
}
