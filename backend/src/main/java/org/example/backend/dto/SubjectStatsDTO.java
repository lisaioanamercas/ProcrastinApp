package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectStatsDTO {
    private String subjectName;
    private int totalTasks;
    private double avgDifficulty;
    private double completionRate;
    private int incompleteTasksDuration;
    private int totalDuration;
}