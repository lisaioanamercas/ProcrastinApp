package org.example.backend.repository;

import org.example.backend.model.HabitCompletion;
import org.example.backend.model.StudyHabit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {

    Optional<HabitCompletion> findByHabitIdAndCompletionDate(Long habitId, LocalDate completionDate);

    List<HabitCompletion> findByHabit_User_IdAndCompletionDateBetween(Long userId, LocalDate start, LocalDate end);

    boolean existsByHabitIdAndCompletionDate(Long habitId, LocalDate completionDate);
    List<HabitCompletion> findByHabit_UserIdAndCompletionDateBetween(Long userId, LocalDate start, LocalDate end);

    List<HabitCompletion> findByHabit_UserIdAndCompletedTrue(Long userId);

}
