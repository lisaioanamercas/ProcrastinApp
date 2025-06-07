package org.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "habit_completions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "completion_date"})
)
public class HabitCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private StudyHabit habit;

    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    @Column(nullable = false)
    private Boolean completed = true;





    public HabitCompletion(StudyHabit habit, LocalDate completionDate) {
        this.habit = habit;
        this.completionDate = completionDate;
        this.completed = true;
    }
}
