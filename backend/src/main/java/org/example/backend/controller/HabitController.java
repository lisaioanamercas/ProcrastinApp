package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
}
