package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateHabitRequest {
    @NotBlank(message = "Habit name is required")
    @Size(max = 255, message = "Habit name must be less than 255 characters")
    private String name;


    @NotBlank(message = "Day of week is required")
    @Size(max = 50, message = "Day of week must be less than 10 characters")
    private String dayOfWeek;


    @NotNull(message = "Time is required")
    private LocalTime time;


}
