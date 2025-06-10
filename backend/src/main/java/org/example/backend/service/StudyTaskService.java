package org.example.backend.service;

import jakarta.transaction.Transactional;
import org.example.backend.dto.CreateTaskRequest;
import org.example.backend.dto.TaskResponse;
import org.example.backend.dto.UpdateTaskRequest;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.StudyTask;
import org.example.backend.model.Subject;
import org.example.backend.model.User;
import org.example.backend.repository.StudyTaskRepository;
import org.example.backend.repository.SubjectRepository;
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

        // Find subject by name
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
        // Găsește task-ul care aparține utilizatorului
        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // Găsește subiectul nou
        // Find subject by name
        Subject subject = subjectRepository.findByName(request.getSubject());
        if (subject == null) {
            throw new ResourceNotFoundException("Subject not found with name: " + request.getSubject());
        }

        // Actualizează task-ul
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
//    public TaskResponse toggleTaskCompletion(Long userId, Long taskId){
//        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
//
//        task.setCompleted(!task.getCompleted());
//        StudyTask updatedTask = studyTaskRepository.save(task);
//        return new TaskResponse(updatedTask);
//    }

    // In your StudyTaskServiceImpl (or equivalent)
    public TaskResponse toggleTaskCompletion(Long userId, Long taskId) {
        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        boolean newStatus = !task.getCompleted();
        task.setCompleted(newStatus);
        if (newStatus) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }
        StudyTask updatedTask = studyTaskRepository.save(task);
        return new TaskResponse(updatedTask);
    }

    public TaskResponse getTaskById(Long userId, Long taskId) {
        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        return new TaskResponse(task);
    }

    public List<List<TaskResponse>> getGroupedTasks(Long userId) {
        List<StudyTask> tasks = studyTaskRepository.findByUserId(userId);

        List<StudyTask> sorted = tasks.stream()
                .sorted(Comparator.comparingInt(this::priorityScore).reversed())
                .collect(Collectors.toList());

        List<List<TaskResponse>> result = new ArrayList<>();
        result.add(sorted.stream().map(TaskResponse::new).collect(Collectors.toList()));
        return result;
    }

    private int priorityScore(StudyTask task) {
        int score = 0;

        if (task.getDeadline() != null) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), task.getDeadline().toLocalDate());
            score += Math.max(0, 30 - (int)days); // cu cât mai aproape, cu atât scor mai mare
        }
        // Dificultate mare => scor mai mare
        if (task.getDifficulty() != null) {
            score += task.getDifficulty() * 10;
        }
        // Task nefinalizat => scor mai mare
        if (!Boolean.TRUE.equals(task.getCompleted())) {
            score += 20;
        }
        return score;
    }


}