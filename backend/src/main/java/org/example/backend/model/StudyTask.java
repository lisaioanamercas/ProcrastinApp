package org.example.backend.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "study_tasks")
public class StudyTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // aici am lazy fetch pentru a nu incarca automat utilizatorul asociat la fiecare interogare
    // dacă ai nevoie de datele utilizatorului, le poți încărca explicit când e necesar

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name= "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private Integer difficulty; // 1-5, unde 1 e brain-dead,  și 5 e foarte greu

    private LocalDateTime deadline;

    @Column(nullable = false)
    private Boolean completed = false; // false by default, task-ul nu este completat

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public StudyTask(User user, Subject subject, String name, String description,
                     Integer durationMinutes, Integer difficulty, LocalDateTime deadline) {
        this.user = user;
        this.subject = subject;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.difficulty = difficulty;
        this.deadline = deadline;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }
}
