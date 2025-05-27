package org.example.backend.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.model.StudyTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Getter
@Setter

public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private String subject_name;
    private Integer duration_minutes;
    private Integer difficulty;
    private LocalDateTime deadline;
    private Boolean completed;
    private LocalDateTime created_at;

    public TaskResponse(StudyTask task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.subject_name = task.getSubject().getName();
        this.duration_minutes = task.getDurationMinutes();
        this.difficulty = task.getDifficulty();
        this.deadline = task.getDeadline();
        this.completed = task.getCompleted();
        this.created_at = task.getCreatedAt();
    }
}
