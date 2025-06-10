package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Tasks", description = "Task management endpoints")
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "Task management endpoints")
// asta e pentru swagger-ui
public class StudyTaskController {

    @Autowired
    private StudyTaskService studyTaskService;

    @Autowired
    private JwtUtils jwtUtils;

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            return jwtUtils.extractUserId(token);
        }
        throw new RuntimeException("Invalid or missing authorization token");
    }

    @Operation(summary = "Get all tasks", description = "Retrieves all tasks for the current user")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
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

    @Operation(summary = "Create a new task", description = "Creates a new study task for the authenticated user.")
    @ApiResponse(responseCode = "201", description = "Task created successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            Long userId = getUserIdFromToken(httpServletRequest);

            System.out.println("Creating task: " + request.getName());
            System.out.println("Duration: " + request.getDuration_minutes());
            System.out.println("Difficulty: " + request.getDifficulty());

            TaskResponse createdTask = studyTaskService.createTask(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Get task by ID", description = "Fetches a specific task by its ID for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
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

    @Operation(summary = "Update task", description = "Updates an existing task with new values.")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @ApiResponse(responseCode = "400", description = "Bad request or invalid data")
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
    @Operation(summary = "Delete task", description = "Deletes the specified task for the authenticated user.")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
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

    @Operation(summary = "Toggle task completion", description = "Marks a task as completed or uncompleted.")
    @ApiResponse(responseCode = "200", description = "Task toggled successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
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

    @GetMapping("/grouped")
    public ResponseEntity<List<TaskResponse>> getGroupedTasks(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        List<TaskResponse> groups = studyTaskService.getGroupedTasks(userId);
        return ResponseEntity.ok(groups);
    }
}
