package com.practice.MyPlatziFlix.exception;

import com.practice.MyPlatziFlix.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Global exception handler for all controllers
 * Provides centralized error handling and consistent error responses
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle Teacher not found exceptions
     */
    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTeacherNotFoundException(
            TeacherNotFoundException exception, HttpServletRequest request) {
        
        log.warn("Teacher not found: {}", exception.getMessage());
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Teacher Not Found")
                .message(exception.getMessage())
                .details("The requested teacher does not exist or may have been deleted")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle Teacher already exists exceptions
     */
    @ExceptionHandler(TeacherAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleTeacherAlreadyExistsException(
            TeacherAlreadyExistsException exception, HttpServletRequest request) {
        
        log.warn("Teacher already exists: {}", exception.getMessage());
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Teacher Already Exists")
                .message(exception.getMessage())
                .details("A teacher with the same email or username already exists")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.CONFLICT);
    }

    /**
     * Handle Teacher inactive (soft deleted) exceptions
     */
    @ExceptionHandler(TeacherInactiveException.class)
    public ResponseEntity<ErrorResponseDTO> handleTeacherInactiveException(
            TeacherInactiveException exception, HttpServletRequest request) {
        
        log.warn("Teacher inactive: {}", exception.getMessage());
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.GONE.value())
                .error("Teacher Inactive")
                .message(exception.getMessage())
                .details("The teacher account has been deactivated or deleted")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.GONE);
    }

    /**
     * Handle validation exceptions (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        
        log.warn("Validation error: {}", exception.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        List<String> errorMessages = new ArrayList<>();
        
        exception.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
                errorMessages.add(String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()));
            } else {
                errorMessages.add(error.getDefaultMessage());
            }
        });
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Input validation failed")
                .details("One or more fields contain invalid data")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .errors(errorMessages)
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
            ConstraintViolationException exception, HttpServletRequest request) {
        
        log.warn("Constraint violation: {}", exception.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        List<String> errorMessages = new ArrayList<>();
        
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            validationErrors.put(fieldName, message);
            errorMessages.add(String.format("%s: %s", fieldName, message));
        }
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Constraint Violation")
                .message("Data constraint validation failed")
                .details("One or more constraints were violated")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .errors(errorMessages)
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle invalid data exceptions
     */
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidDataException(
            InvalidDataException exception, HttpServletRequest request) {
        
        log.warn("Invalid data: {}", exception.getMessage());
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Data")
                .message(exception.getMessage())
                .details("The provided data is invalid or does not meet business requirements")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle database constraint violations
     */
    @ExceptionHandler({DatabaseConstraintException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponseDTO> handleDatabaseConstraintException(
            Exception exception, HttpServletRequest request) {
        
        log.error("Database constraint violation: {}", exception.getMessage(), exception);
        
        String message = "Database constraint violation";
        String details = "The operation could not be completed due to data integrity constraints";
        
        if (exception instanceof DatabaseConstraintException) {
            message = exception.getMessage();
        } else if (exception.getMessage() != null) {
            if (exception.getMessage().contains("email")) {
                message = "Email address is already in use";
                details = "A teacher with this email address already exists";
            } else if (exception.getMessage().contains("username")) {
                message = "Username is already taken";
                details = "A teacher with this username already exists";
            }
        }
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Data Integrity Violation")
                .message(message)
                .details(details)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.CONFLICT);
    }

    /**
     * Handle malformed JSON requests
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        
        log.warn("Malformed JSON request: {}", exception.getMessage());
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Malformed Request")
                .message("Invalid JSON format")
                .details("The request body contains malformed JSON or missing required data")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception, HttpServletRequest request) {
        
        log.warn("Missing request parameter: {}", exception.getMessage());
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Missing Parameter")
                .message(String.format("Required parameter '%s' is missing", exception.getParameterName()))
                .details("Please provide all required parameters")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle method argument type mismatch
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        
        log.warn("Method argument type mismatch: {}", exception.getMessage());
        
        String message = String.format("Invalid value '%s' for parameter '%s'", exception.getValue(), exception.getName());
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Parameter Type")
                .message(message)
                .details("Parameter type does not match expected format")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle 404 - No handler found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundException(
            NoHandlerFoundException exception, HttpServletRequest request) {
        
        log.warn("No handler found: {}", exception.getMessage());
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Endpoint Not Found")
                .message(String.format("No endpoint found for %s %s", exception.getHttpMethod(), exception.getRequestURL()))
                .details("The requested API endpoint does not exist")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception exception, HttpServletRequest request) {
        
        log.error("Unexpected error: {}", exception.getMessage(), exception);
        
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .details("Please try again later or contact support if the problem persists")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .requestId(UUID.randomUUID().toString())
                .build();
        
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}