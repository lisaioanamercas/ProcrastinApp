// src/main/java/org/example/backend/service/ActivityService.java
package org.example.backend.service;

import org.example.backend.model.StudyTask;
import org.example.backend.model.HabitCompletion;
import org.example.backend.repository.StudyTaskRepository;
import org.example.backend.repository.HabitCompletionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {

    @Autowired
    private StudyTaskRepository studyTaskRepository;

    @Autowired
    private HabitCompletionRepository habitCompletionRepository;

    public Map<String, Object> getMonthlyActivity(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        Map<String, Map<String, Object>> result = new HashMap<>();
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            String date = LocalDate.of(year, month, day).toString();
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("tasks_completed", 0);
            dayData.put("habits_completed", 0);
            dayData.put("total_activity", 0);
            dayData.put("level", 0);
            result.put(date, dayData);
        }

        // Use completedAt instead of deadline
        List<StudyTask> completedTasks = studyTaskRepository
                .findByUserIdAndCompletedIsTrueAndCompletedAtBetween(
                        userId,
                        start.atStartOfDay(),
                        end.atTime(23, 59, 59)
                );
        for (StudyTask task : completedTasks) {
            if (task.getCompletedAt() != null) {
                String date = task.getCompletedAt().toLocalDate().toString();
                Map<String, Object> dayData = result.get(date);
                if (dayData != null) {
                    int tasks = (int) dayData.get("tasks_completed") + 1;
                    dayData.put("tasks_completed", tasks);
                }
            }
        }

        // Habits logic remains unchanged
        List<HabitCompletion> completions = habitCompletionRepository
                .findByHabit_User_IdAndCompletionDateBetween(userId, start, end);
        for (HabitCompletion hc : completions) {
            if (hc.getCompleted()) {
                String date = hc.getCompletionDate().toString();
                Map<String, Object> dayData = result.get(date);
                if (dayData != null) {
                    int habits = (int) dayData.get("habits_completed") + 1;
                    dayData.put("habits_completed", habits);
                }
            }
        }

        for (Map<String, Object> dayData : result.values()) {
            int total = (int) dayData.get("tasks_completed") + (int) dayData.get("habits_completed");
            dayData.put("total_activity", total);
            dayData.put("level", getActivityLevel(total));
        }

        Map<String, Object> flat = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
            flat.put(entry.getKey(), entry.getValue());
        }
        return flat;
    }


    private int getActivityLevel(int activityCount) {
        if (activityCount == 0) return 0;
        if (activityCount <= 2) return 1;
        if (activityCount <= 4) return 2;
        if (activityCount <= 7) return 3;
        return 4;
    }
}