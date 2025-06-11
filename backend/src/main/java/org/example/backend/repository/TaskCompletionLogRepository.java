package org.example.backend.repository;

import org.example.backend.model.TaskCompletionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskCompletionLogRepository extends JpaRepository<TaskCompletionLog, Long> {
    List<TaskCompletionLog> findByUserIdOrderByCompletionDateDesc(Long userId);

    void deleteByUserIdAndTaskId(Long userId, Long taskId);
    void deleteByUserIdAndCompletionDate(Long userId, LocalDate completionDate);


}