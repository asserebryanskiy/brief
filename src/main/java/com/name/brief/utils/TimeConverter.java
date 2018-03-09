package com.name.brief.utils;

import java.time.Duration;

public class TimeConverter {

    /**
     * Takes timeStr of format mm:ss as a parameter and returns
     * appropriate Duration object.
     * */
    public static Duration getDurationFromTimeStr(String timeStr) {
        int delimiterIndex = timeStr.indexOf(":");
        long minutes = Long.parseLong(timeStr.substring(0, delimiterIndex));
        long seconds = Long.parseLong(timeStr.substring(delimiterIndex + 1));
        return Duration.ofMinutes(minutes).plusSeconds(seconds);
    }

    /**
     * Takes duration as a parameter and returns timeStr formatted as mm:ss.
     * */
    public static String getTimeStrFromDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
