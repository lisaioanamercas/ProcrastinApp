package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.backend.dto.CreateTaskRequest;
import org.example.backend.dto.TaskResponse;
import org.example.backend.dto.UpdateTaskRequest;
import org.example.backend.security.jwt.JwtUtils;
import org.example.backend.service.StudyTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class StudyTaskController {

    @Autowired
    private StudyTaskService studyTaskService;

    @Autowired
    private JwtUtils jwtUtils;

    // extrage userId din JWT token
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            return jwtUtils.extractUserId(token);
        }
        throw new RuntimeException("Invalid or missing authorization token");
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getUserTasks(HttpServletRequest request) {
        try{
            Long userId = getUserIdFromToken(request);
            List<TaskResponse> tasks = studyTaskService.getUserTasks(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // creez un nou task pentru user ul autentificat
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            Long userId = getUserIdFromToken(httpServletRequest);

            // Debug
            System.out.println("Creating task: " + request.getName());
            System.out.println("Subject name: " + request.getSubject());
            System.out.println("Duration: " + request.getDuration_minutes());
            System.out.println("Difficulty: " + request.getDifficulty());

            TaskResponse createdTask = studyTaskService.createTask(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //get task by id -> da retreive la un task dupa id
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long taskId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            TaskResponse task = studyTaskService.getTaskById(userId, taskId);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //da update la un task
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest taskRequest,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            TaskResponse updatedTask = studyTaskService.updateTask(userId, taskId, taskRequest);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // delete la un task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            studyTaskService.deleteTask(userId, taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // mark a task as completed (toggle)
    @PatchMapping("/{taskId}/toggle")
    public ResponseEntity<TaskResponse> toggleTaskCompletion(
            @PathVariable Long taskId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            TaskResponse updatedTask = studyTaskService.toggleTaskCompletion(userId, taskId);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
