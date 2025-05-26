package org.example.backend.controller;

import org.example.backend.model.Subject;
import org.example.backend.repository.SubjectRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "http://localhost:3000")
public class SubjectController {
    private final SubjectRepository subjectRepository;

    public SubjectController(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
}
