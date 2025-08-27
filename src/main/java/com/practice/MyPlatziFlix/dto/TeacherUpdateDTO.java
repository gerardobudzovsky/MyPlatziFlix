package com.practice.MyPlatziFlix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating Teacher entities
 * All fields optional; presence implies update
 */
@Getter
@Setter
public class TeacherUpdateDTO {

    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes, and hyphens")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes, and hyphens")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email; // optional

    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain letters, numbers, dots, hyphens, and underscores")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username; // optional

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password; // optional

    @Size(min = 10, max = 2000, message = "Biography must be between 10 and 2000 characters when provided")
    private String biography;

    @Pattern(regexp = "^[a-zA-Z0-9\\s&+.-]+$", message = "Specialization can only contain letters, numbers, spaces, and common symbols (&, +, ., -)")
    @Size(min = 3, max = 100, message = "Specialization must be between 3 and 100 characters when provided")
    private String specialization;
}


