package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.backend.dto.CreateHabitRequest;
import org.example.backend.dto.HabitsResponse;
import org.example.backend.security.jwt.JwtUtils;
import org.example.backend.service.HabitTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Habits", description = "Endpoints for managing study habits")
@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = "*")
public class HabitController {

    @Autowired
    private HabitTaskService habitTaskService;

    @Autowired
    private JwtUtils jwtUtils;

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            System.out.println("userId: " + username);
            return jwtUtils.extractUserId(token);
        }
        throw new RuntimeException("Invalid or missing authorization token");
    }

    @Operation(summary = "Get all habits", description = "Retrieves all study habits for the current user")
    @ApiResponse(responseCode = "200", description = "Habits retrieved successfully")
    @GetMapping
    public ResponseEntity<List<HabitsResponse>> getUserHabits(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<HabitsResponse> habits = habitTaskService.getUserHabits(userId);
            return ResponseEntity.ok(habits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Create a new habit", description = "Creates a new study habit for the authenticated user.")
    @ApiResponse(responseCode = "201", description = "Habit created successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @PostMapping
    public ResponseEntity<HabitsResponse> createHabit(
            @Valid @RequestBody CreateHabitRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            Long userId = getUserIdFromToken(httpServletRequest);

            System.out.println("Creating habit: " + request.getName());
            System.out.println("Day of week: " + request.getDayOfWeek());

            HabitsResponse createdHabit = habitTaskService.createHabit(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdHabit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Delete a habit", description = "Deletes a study habit by id for the authenticated user.")
    @ApiResponse(responseCode = "204", description = "Habit deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            habitTaskService.deleteHabit(userId, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Toggle habit completion", description = "Marks a habit as completed or uncompleted for today")
    @ApiResponse(responseCode = "200", description = "Habit toggled successfully")
    @ApiResponse(responseCode = "404", description = "Habit not found")
    @PatchMapping("/{habitId}/toggle")
    public ResponseEntity<Void> toggleHabitCompletion(
            @PathVariable Long habitId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            habitTaskService.toggleHabitCompletion(userId, habitId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
