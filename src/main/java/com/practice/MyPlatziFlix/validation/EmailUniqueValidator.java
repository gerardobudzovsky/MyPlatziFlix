package com.practice.MyPlatziFlix.validation;

import com.practice.MyPlatziFlix.repository.TeacherRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validator for email uniqueness
 */
@Component
@RequiredArgsConstructor
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, String> {

    private final TeacherRepository teacherRepository;

    @Override
    public void initialize(EmailUnique constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }
        
        // Check if email exists in database
        return !teacherRepository.findByEmail(email).isPresent();
    }
}