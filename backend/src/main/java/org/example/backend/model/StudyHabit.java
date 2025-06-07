package org.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "study_habits")
public class StudyHabit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "day_of_week", length = 10)
    private String dayOfWeek;

    @Column(name = "time")
    private LocalTime time;

    @Column(nullable = false)
    private Boolean recurring = true;

    public StudyHabit(User user, Subject subject, String name, String description, String dayOfWeek, LocalTime time) {
        this.user = user;
        this.subject = subject;
        this.name = name;
        this.description = description;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.recurring = true;
    }
}
