package org.example.backend.service;


import jakarta.transaction.Transactional;
import org.example.backend.dto.*;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.*;
import org.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatsService {

    @Autowired
    private StudyTaskRepository studyTaskRepository;

    @Autowired
    private StudyHabitRepository studyHabitRepository;

    public double getAvgDifficulty(Long userId) {
        List<StudyTask> tasks = studyTaskRepository.findByUserId(userId);
        return tasks.stream()
                .filter(t -> t.getDifficulty() != null)
                .mapToInt(StudyTask::getDifficulty)
                .average()
                .orElse(0.0);
    }

    public double getAvgDuration(Long userId) {
        List<StudyTask> tasks = studyTaskRepository.findByUserId(userId);
        return tasks.stream()
                .mapToInt(StudyTask::getDurationMinutes)
                .average()
                .orElse(0.0) / 60.0;
    }

    // 4. Longest Streak (habits)
  /*  public int getLongestStreak(Long userId) {
        List<StudyHabit> habits = studyHabitRepository.findByUserId(userId);
        return habits.stream()
                .mapToInt(h -> h.getStreak() != null ? h.getStreak() : 0)
                .max()
                .orElse(0);
    }*/

    // 5. Metodă pentru a returna toate statisticile într-un DTO
    public StudyStatsResponse getStatsForUser(Long userId) {
        StudyStatsResponse resp = new StudyStatsResponse();
     //   resp.setTasksThisWeek(getTasksThisWeek(userId));
        resp.setAvgDifficulty(getAvgDifficulty(userId));
        resp.setAvgDuration(getAvgDuration(userId));
    //    resp.setLongestStreak(getLongestStreak(userId));
        return resp;
    }




}
