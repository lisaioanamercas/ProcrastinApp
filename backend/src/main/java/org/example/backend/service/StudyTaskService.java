package org.example.backend.service;

import jakarta.transaction.Transactional;
import org.example.backend.dto.CreateTaskRequest;
import org.example.backend.dto.TaskResponse;
import org.example.backend.dto.UpdateTaskRequest;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.StudyTask;
import org.example.backend.model.Subject;
import org.example.backend.model.TaskCompletionLog;
import org.example.backend.model.User;
import org.example.backend.repository.StudyTaskRepository;
import org.example.backend.repository.SubjectRepository;
import org.example.backend.repository.TaskCompletionLogRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudyTaskService {

    @Autowired
    private StudyTaskRepository studyTaskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TaskCompletionLogRepository completionLogRepository;

    public List<TaskResponse> getUserTasks(Long userId) {
        List<StudyTask> tasks = studyTaskRepository.findByUserId(userId);
        return tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse createTask(Long userId, CreateTaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Subject subject = subjectRepository.findByName(request.getSubject());
        if (subject == null) {
            throw new ResourceNotFoundException("Subject not found with name: " + request.getSubject());
        }

        StudyTask task = new StudyTask();
        task.setUser(user);
        task.setSubject(subject);
        task.setName(request.getName().trim());
        task.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        task.setDurationMinutes(request.getDuration_minutes());
        task.setDifficulty(request.getDifficulty());
        task.setDeadline(request.getDeadline());

        StudyTask savedTask = studyTaskRepository.save(task);
        return new TaskResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long userId, Long taskId, UpdateTaskRequest request) {
        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        Subject subject = subjectRepository.findByName(request.getSubject());
        if (subject == null) {
            throw new ResourceNotFoundException("Subject not found with name: " + request.getSubject());
        }

        // Actualizeaza task-ul
        task.setName(request.getName().trim());
        task.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        task.setSubject(subject);
        task.setDurationMinutes(request.getDuration_minutes());
        task.setDifficulty(request.getDifficulty());
        task.setDeadline(request.getDeadline());

        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }

        StudyTask updatedTask = studyTaskRepository.save(task);
        return new TaskResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long userId, Long taskId){
        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        studyTaskRepository.delete(task);
    }

    @Transactional
    public TaskResponse toggleTaskCompletion(Long userId, Long taskId) {
        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean newStatus = !task.getCompleted();
        task.setCompleted(newStatus);

        if (newStatus) {
            LocalDateTime now = LocalDateTime.now();
            task.setCompletedAt(now);

            //aici e logica de logger la care am semi renuntat
            TaskCompletionLog log = new TaskCompletionLog();
            log.setUserId(userId);
            log.setTaskId(taskId);
            log.setCompletionDate(now.toLocalDate());
            completionLogRepository.save(log);

        } else {
            task.setCompletedAt(null);
            completionLogRepository.deleteByUserIdAndTaskId(userId, taskId);
        }
        StudyTask updatedTask = studyTaskRepository.save(task);
        return new TaskResponse(updatedTask);
    }

    public TaskResponse getTaskById(Long userId, Long taskId) {
        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        return new TaskResponse(task);
    }

    public List<TaskResponse> getGroupedTasks(Long userId) {
        List<StudyTask> tasks = studyTaskRepository.findByUserId(userId);

        return tasks.stream()
                .sorted(Comparator.comparingInt(this::priorityScore).reversed())
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    private int priorityScore(StudyTask task) {
        if (Boolean.TRUE.equals(task.getCompleted())) {
            return Integer.MIN_VALUE;
        }

        int score = 0;

        if (task.getDurationMinutes() != 0) {
            int duration = task.getDurationMinutes();

            if (task.getDeadline() != null) {
                long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), task.getDeadline().toLocalDate());

                if (daysLeft <= 1 && duration <= 30) {
                    score += 40;
                }
            }

            if (duration <= 30) {
                score += 10;
            } else if (duration >= 120) {
                score -= 10;
            }
        }

        if (task.getDifficulty() != null) {
            score += task.getDifficulty() * 10;
        }
        score += 20;

        return score;
    }


}