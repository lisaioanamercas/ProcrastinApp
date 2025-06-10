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
public class HabitTaskService {

    @Autowired
    private StudyHabitRepository studyHabitRepository;

    @Autowired
    private UserRepository userRepository;

   /* @Autowired
    private SubjectRepository subjectRepository;*/

    @Autowired
    private HabitCompletionRepository habitCompletionRepository;



    public List<HabitsResponse> getUserHabits(Long userId) {
        List<StudyHabit> tasks = studyHabitRepository.findByUserId(userId);
        String currentDay = LocalDate.now().getDayOfWeek().name().toLowerCase();

        List<StudyHabit> filtered =  tasks.stream()
                .filter(h -> Arrays.asList(h.getDayOfWeek().split(","))
                        .contains(currentDay))
                .collect(Collectors.toList());

        return filtered.stream()
                .map(habit -> {
                    HabitsResponse resp = new HabitsResponse(habit);
                    boolean completedToday = habitCompletionRepository
                            .findByHabitIdAndCompletionDate(habit.getId(), LocalDate.now())
                            .isPresent();
                    resp.setCompletedToday(completedToday);
                    return resp;
                })
                .collect(Collectors.toList());
    }

       public HabitsResponse createHabit(Long userId, CreateHabitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        StudyHabit habit = new StudyHabit();
        habit.setName(request.getName());
        habit.setDayOfWeek(request.getDayOfWeek());
        habit.setTime(request.getTime());
        habit.setUser(user);

        StudyHabit savedHabit = studyHabitRepository.save(habit);

        HabitsResponse response = new HabitsResponse(savedHabit);
        return response;
    }
    public void deleteHabit(Long userId, Long habitId) {
    StudyHabit habit = studyHabitRepository.findById(habitId)
        .orElseThrow(() -> new RuntimeException("Habit not found"));
    if (!habit.getUser().getId().equals(userId)) {
        throw new RuntimeException("Unauthorized");
    }
    studyHabitRepository.delete(habit);
}
    public void toggleHabitCompletion(Long userId, Long habitId) {
        StudyHabit habit = studyHabitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        if (!habit.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        LocalDate today = LocalDate.now();
        Optional<HabitCompletion> completionOpt = habitCompletionRepository
                .findByHabitIdAndCompletionDate(habitId, today);

        if (completionOpt.isPresent()) {
            habitCompletionRepository.delete(completionOpt.get());
        } else {
            HabitCompletion completion = new HabitCompletion();
            completion.setHabit(habit);
            completion.setCompleted(true);
            completion.setCompletionDate(today);
            habitCompletionRepository.save(completion);
        }
    }


}
