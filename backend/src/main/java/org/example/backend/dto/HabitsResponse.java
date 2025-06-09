package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.model.StudyHabit;

import java.time.LocalTime;

@Getter
@Setter
public class HabitsResponse {

    private Long id;
    private String name;
    private String description;
    private String dayOfWeek;
    private Boolean recurring;
    private LocalTime time;
  ///  private Long subjectId;
    private Long userId;
    private boolean completedToday;


    public HabitsResponse(StudyHabit habit) {

        this.id = habit.getId();
        this.name = habit.getName();
        this.description = habit.getDescription();
        this.dayOfWeek = habit.getDayOfWeek();
        this.recurring = habit.getRecurring();
        this.time = habit.getTime();
      //  this.subjectId = habit.getSubject().getId();
        this.userId = habit.getUser().getId();
    }

}
