package com.name.brief.web.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Is used to transfer between server and client information about players statistic
 * through all rounds.
 */
public class StatsList {
    private List<Statistic> statistics;

    @Data
    public static class Statistic {
        private String commandName;
        private SortedMap<Integer, Integer> roundScoreMap;
        private int totalScore;

        public Statistic(String commandName, SortedMap<Integer, Integer> roundScoreMap) {
            this.commandName = commandName;
            this.roundScoreMap = roundScoreMap;
            totalScore = roundScoreMap.values().stream().mapToInt(i -> i).sum();
        }
    }

    public StatsList() {
        statistics = new ArrayList<>();
    }

    public StatsList(int listSize) {
        statistics = new ArrayList<>(listSize);
    }


    public void addStatistic(String commandName, SortedMap<Integer, Integer> roundScoreMap) {
        statistics.add(new Statistic(commandName, roundScoreMap));
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }
}
