package org.example.backend;

import org.example.backend.service.StatsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner populateTestData(StatsService statsService) {
//        return args -> {
//            // Create a streak from June 1-8, 2024, skipping June 9
//            statsService.simulateHabitCompletionStreak(
//                    1L, // User ID = 1
//                    LocalDate.of(2024, 6, 1),
//                    LocalDate.of(2024, 6, 9)
//            );
//
//            System.out.println("Habit completion streak simulated for user ID 1");
//        };
//    }
}
