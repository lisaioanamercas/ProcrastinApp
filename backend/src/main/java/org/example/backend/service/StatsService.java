package org.example.backend.service;


import jakarta.transaction.Transactional;
import org.example.backend.dto.*;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.*;
import org.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    @Autowired
    private StudyTaskRepository studyTaskRepository;

    @Autowired
    private StudyHabitRepository studyHabitRepository;

    @Autowired
    private HabitCompletionRepository habitCompletionRepository;

    @Autowired
    private TaskCompletionLogRepository completionLogRepository;

    public int getWeeklyTaskCount(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday
        LocalDate endOfWeek = startOfWeek.plusDays(6); // Sunday

        // Count completed tasks
        int taskCount = studyTaskRepository.countByUserIdAndCompletedIsTrueAndCompletedAtBetween(
                userId,
                startOfWeek.atStartOfDay(),
                endOfWeek.atTime(23, 59, 59)
        );

        // Count habit completions for the current week
        List<HabitCompletion> habitCompletions = habitCompletionRepository.findByHabit_UserIdAndCompletionDateBetween(
                userId, startOfWeek, endOfWeek);
        int habitCount = habitCompletions.size();

        return taskCount + habitCount;
    }

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
    //AICI SE FOLOSESTE LOGGER UL ALA COOL DAR MOMENTAN AM DAT-O IN BARA
//    public int getCurrentStreak(Long userId) {
//        // Get all activity completions for this user
//        List<TaskCompletionLog> completions = completionLogRepository.findByUserIdOrderByCompletionDateDesc(userId);
//
//        if (completions.isEmpty()) return 0;
//
//        // Group by date to handle multiple completions per day
//        Set<LocalDate> completionDates = completions.stream()
//                .map(TaskCompletionLog::getCompletionDate)
//                .collect(Collectors.toSet());
//
//        // Calculate current streak (consecutive days from today backwards)
//        int currentStreak = 0;
//        LocalDate currentDate = LocalDate.now();
//
//        while (completionDates.contains(currentDate)) {
//            currentStreak++;
//            currentDate = currentDate.minusDays(1);
//        }
//
//        return currentStreak;
//    }
    public int getCurrentStreak(Long userId) {
        List<TaskCompletionLog> completions = completionLogRepository.findByUserIdOrderByCompletionDateDesc(userId);
        if (completions.isEmpty()) return 0;

        // Group by date and sort descending
        List<LocalDate> sortedDates = completions.stream()
                .map(TaskCompletionLog::getCompletionDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // If the most recent date is today, count backward
        if (sortedDates.get(0).equals(LocalDate.now())) {
            int streak = 1;
            LocalDate expectedPrevDate = LocalDate.now();

            for (int i = 1; i < sortedDates.size(); i++) {
                expectedPrevDate = expectedPrevDate.minusDays(1);
                if (sortedDates.get(i).equals(expectedPrevDate)) {
                    streak++;
                } else {
                    break;
                }
            }
            return streak;
        } else {
            // If today has no completions, look at the most recent streak
            int streak = 1;
            LocalDate mostRecent = sortedDates.get(0);

            for (int i = 1; i < sortedDates.size(); i++) {
                if (sortedDates.get(i).equals(mostRecent.minusDays(i))) {
                    streak++;
                } else {
                    break;
                }
            }
            return streak;
        }
    }

    public int getLongestStreak(Long userId) {
        // Get all activity completions for this user (both tasks and habits)
        List<TaskCompletionLog> completions = completionLogRepository.findByUserIdOrderByCompletionDateDesc(userId);

        if (completions.isEmpty()) return 0;

        // Group by date to handle multiple completions per day
        Set<LocalDate> completionDates = completions.stream()
                .map(TaskCompletionLog::getCompletionDate)
                .collect(Collectors.toSet());

        // Calculate current streak (consecutive days from today backwards)
        int currentStreak = 0;
        int maxStreak = 0;
        LocalDate currentDate = LocalDate.now();

        while (completionDates.contains(currentDate)) {
            currentStreak++;
            currentDate = currentDate.minusDays(1);
        }

        maxStreak = Math.max(maxStreak, currentStreak);

        // Look for past streaks
        List<LocalDate> sortedDates = new ArrayList<>(completionDates);
        Collections.sort(sortedDates);

        currentStreak = 1;
        for (int i = 1; i < sortedDates.size(); i++) {
            if (sortedDates.get(i).isEqual(sortedDates.get(i-1).plusDays(1))) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }

        return maxStreak;
    }
    // 5. Metodă pentru a returna toate statisticile într-un DTO
    public StudyStatsResponse getStatsForUser(Long userId) {
        StudyStatsResponse resp = new StudyStatsResponse();
        resp.setTasksThisWeek(getWeeklyTaskCount(userId)); // Uncomment this line
        resp.setAvgDifficulty(getAvgDifficulty(userId));
        resp.setAvgDuration(getAvgDuration(userId));
        resp.setLongestStreak(getLongestStreak(userId));
        resp.setCurrentStreak(getCurrentStreak(userId)); // Add this line

        return resp;
    }



    @Transactional
    public void simulateHabitCompletionStreak(Long userId, LocalDate startDate, LocalDate endDate, LocalDate... skipDates) {
        // Convert skipDates to a Set for efficient lookups
        Set<LocalDate> datesToSkip = new HashSet<>(Arrays.asList(skipDates));

        // Get a habit for this user
        StudyHabit habit = studyHabitRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No habits found for user: " + userId));

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Skip if this date is in the skip set
            if (!datesToSkip.contains(currentDate)) {
                // Check if completion already exists for this habit and date
                boolean exists = habitCompletionRepository.existsByHabitIdAndCompletionDate(habit.getId(), currentDate);

                if (!exists) {
                    // Create a new HabitCompletion only if it doesn't exist
                    HabitCompletion completion = new HabitCompletion(habit, currentDate);
                    habitCompletionRepository.save(completion);
                }
            }

            currentDate = currentDate.plusDays(1);
        }
    }

}
