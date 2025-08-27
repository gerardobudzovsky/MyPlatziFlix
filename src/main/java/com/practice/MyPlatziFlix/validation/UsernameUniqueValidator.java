package com.practice.MyPlatziFlix.validation;

import com.practice.MyPlatziFlix.repository.TeacherRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validator for username uniqueness
 */
@Component
@RequiredArgsConstructor
public class UsernameUniqueValidator implements ConstraintValidator<UsernameUnique, String> {

    private final TeacherRepository teacherRepository;

    @Override
    public void initialize(UsernameUnique constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }
        
        // Check if username exists in database
        return !teacherRepository.findByUsername(username).isPresent();
    }
}