package org.example.backend.service;

import jakarta.transaction.Transactional;
import org.example.backend.dto.*;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.StudyTask;
import org.example.backend.model.Subject;
import org.example.backend.model.User;
import org.example.backend.repository.StudyHabitRepository;
import org.example.backend.repository.StudyTaskRepository;
import org.example.backend.repository.SubjectRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.backend.model.StudyHabit;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HabitTaskService {

    @Autowired
    private StudyHabitRepository studyHabitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;


    public List<HabitsResponse> getUserHabits(Long userId) {
        List<StudyHabit> tasks = studyHabitRepository.findByUserId(userId);

        return tasks.stream()
                .map(HabitsResponse::new)
                .collect(Collectors.toList());
    }

       public HabitsResponse createHabit(Long userId, CreateHabitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
           Subject subject = subjectRepository.findByName(request.getSubject());
           if (subject == null) {
               throw new ResourceNotFoundException("Subject not found with name: " + request.getSubject());
           }

        StudyHabit habit = new StudyHabit();
        habit.setName(request.getName());
        habit.setDayOfWeek(request.getDayOfWeek());
     //   habit.setRecurring(request.getRecurring());
        habit.setTime(request.getTime()); // dacă folosești LocalTime, adaptează tipul
        habit.setSubject(subject);
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

}
