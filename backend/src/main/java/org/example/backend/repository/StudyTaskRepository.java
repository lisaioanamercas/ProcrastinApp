package org.example.backend.repository;

import org.example.backend.model.StudyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;


import java.util.List;
import java.util.Optional;

@Repository
public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {
    List<StudyTask> findByUserId(Long userId);

    List<StudyTask> findByUserIdAndCompletedOrderByDeadlineAsc(Long userId, Boolean completed);

    @Query("SELECT t FROM StudyTask t WHERE t.user.id = :userId AND t.id = :taskId")
    Optional<StudyTask> findByIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);

    List<StudyTask> findByUserIdAndCompletedIsTrueAndCompletedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    int countByUserIdAndCompletedIsTrueAndCompletedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<StudyTask> findByUserIdAndCompletedIsTrue(Long userId);

}