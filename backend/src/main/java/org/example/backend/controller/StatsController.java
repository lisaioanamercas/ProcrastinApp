package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.StudyStatsResponse;
import org.example.backend.dto.TasksBySubjectResponse;
import org.example.backend.security.jwt.JwtUtils;
import org.example.backend.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stats", description = "Endpoints for managing study stats")
@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private StatsService statsService;

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

    @Operation(summary = "Get all stats", description = "Retrieves all study stats for the current user")
    @ApiResponse(responseCode = "200", description = "Stats retrieved successfully")
    @GetMapping("/study")

    public ResponseEntity<StudyStatsResponse> getStudyStats(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            StudyStatsResponse stats = statsService.getStatsForUser(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    @GetMapping("/download")
    public ResponseEntity<String> downloadStatsAsText(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            StudyStatsResponse stats = statsService.getStatsForUser(userId);

            // Create a formatted text representation
            StringBuilder textContent = new StringBuilder();
            textContent.append("Study Statistics\n");
            textContent.append("===============\n\n");
            textContent.append("Tasks This Week: ").append(stats.getTasksThisWeek()).append("\n");
            textContent.append("Average Difficulty: ").append(stats.getAvgDifficulty()).append("\n");
            textContent.append("Average Duration: ").append(stats.getAvgDuration()).append(" minutes\n");
            textContent.append("Current Streak: ").append(stats.getCurrentStreak()).append(" days\n");
            textContent.append("Longest Streak: ").append(stats.getLongestStreak()).append(" days\n");

            // Set headers for text file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "study_stats.txt");

            return new ResponseEntity<>(textContent.toString(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping(value = "/by-subject/download", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TasksBySubjectResponse> downloadStatsBySubject(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            TasksBySubjectResponse stats = statsService.getTaskStatsBySubject(userId);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subject_stats.json");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-subject")
    public ResponseEntity<TasksBySubjectResponse> getStatsBySubject(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            TasksBySubjectResponse stats = statsService.getTaskStatsBySubject(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
