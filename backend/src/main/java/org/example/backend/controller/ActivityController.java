package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.backend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.security.jwt.JwtUtils;

import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

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

    @Operation(
            summary = "User login",
            description = "Authenticates user and returns a JWT access token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful and token issued"),
                    @ApiResponse(responseCode = "400", description = "User not found or invalid credentials")
            }
    )
    @GetMapping("/heatmap")
    public ResponseEntity<Map<String, Object>> getHeatmap(
            @RequestParam int year,
            @RequestParam int month,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);

        Map<String, Object> data = activityService.getMonthlyActivity(userId, year, month);
        return ResponseEntity.ok(data);
    }
}