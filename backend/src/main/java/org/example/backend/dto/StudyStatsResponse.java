package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyStatsResponse {


    private int tasksThisWeek;
    private double avgDifficulty;
    private double avgDuration;
    private int longestStreak;
    private int currentStreak;

}