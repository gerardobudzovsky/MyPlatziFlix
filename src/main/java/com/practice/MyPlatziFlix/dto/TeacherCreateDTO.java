package com.practice.MyPlatziFlix.dto;

import com.practice.MyPlatziFlix.validation.EmailUnique;
import com.practice.MyPlatziFlix.validation.UsernameUnique;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating Teacher entities
 */
@Getter
@Setter
public class TeacherCreateDTO {

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes, and hyphens")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes, and hyphens")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @EmailUnique
    private String email;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain letters, numbers, dots, hyphens, and underscores")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @UsernameUnique
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @Size(min = 10, max = 2000, message = "Biography must be between 10 and 2000 characters when provided")
    private String biography;

    @Pattern(regexp = "^[a-zA-Z0-9\\s&+.-]+$", message = "Specialization can only contain letters, numbers, spaces, and common symbols (&, +, ., -)")
    @Size(min = 3, max = 100, message = "Specialization must be between 3 and 100 characters when provided")
    private String specialization;
}



