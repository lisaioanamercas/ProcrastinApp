package org.example.backend.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.backend.model.Subject;
import org.example.backend.repository.SubjectRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Subjects", description = "Endpoints for retrieving subjects")
@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "http://localhost:5500")
public class SubjectController {
    private final SubjectRepository subjectRepository;

    public SubjectController(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
    @Operation(summary = "Get all subjects", description = "Returns a list of all subjects available in the database.")
    @ApiResponse(responseCode = "200", description = "Subjects retrieved successfully")
    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
}
