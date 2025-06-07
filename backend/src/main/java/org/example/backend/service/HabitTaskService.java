package org.example.backend.service;

import jakarta.transaction.Transactional;
import org.example.backend.dto.CreateTaskRequest;
import org.example.backend.dto.HabitsResponse;
import org.example.backend.dto.TaskResponse;
import org.example.backend.dto.UpdateTaskRequest;
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

    public List<HabitsResponse> getUserHabits(Long userId) {
        List<StudyHabit> tasks = studyHabitRepository.findByUserId(userId);
        return tasks.stream()
                .map(HabitsResponse::new)
                .collect(Collectors.toList());
    }

}
