package org.example.backend.repository;

import org.example.backend.model.StudyHabit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyHabitRepository extends JpaRepository<StudyHabit, Long> {
    List<StudyHabit> findByUserId(Long userId);
    Optional<StudyHabit> findFirstByUserId(Long userId);
}
