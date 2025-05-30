package org.example.backend.repository;

import org.example.backend.model.StudyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

    List<StudyTask> findByUserIdOrderByDeadlineAsc(Long userId);

    List<StudyTask> findByUserIdAndCompletedOrderByDeadlineAsc(Long userId, Boolean completed);

    @Query("SELECT t FROM StudyTask t WHERE t.user.id = :userId AND t.id = :taskId")
    Optional<StudyTask> findByIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM StudyTask t WHERE t.user.id = :userId AND t.completed = true")
    Long countCompletedByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM StudyTask t WHERE t.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}

// momentan am metode pentru: gasirea taskurilor per utilizaot -- query uri pentru statisitici -- gasirea taskurilor dupa ID si userID :)
