package org.example.backend.repository;

import org.example.backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // This interface will automatically provide CRUD operations for the Subject entity
    // No additional methods are needed unless custom queries are required
    Subject findByName(String name);
}
