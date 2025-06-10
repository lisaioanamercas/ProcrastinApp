package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TasksBySubjectResponse {
    private List<SubjectStatsDTO> subjects = new ArrayList<>();
}