package com.practice.MyPlatziFlix.dto;

import com.practice.MyPlatziFlix.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for Teacher entity
 * Includes public identifier; excludes sensitive information like password
 */
@Getter
@Setter
public class TeacherResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String biography;
    private String specialization;
    private Map<String, String> courses; // Course slug as key and name as value

    /**
     * Returns the full name of the teacher
     */
    public String getFullName() {
        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";

        if (first.isEmpty() && last.isEmpty()) {
            return "";
        }
        if (first.isEmpty()) {
            return last;
        }
        if (last.isEmpty()) {
            return first;
        }
        return first + " " + last;
    }
}