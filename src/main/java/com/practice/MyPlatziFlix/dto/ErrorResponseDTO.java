package com.practice.MyPlatziFlix.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized error response DTO for all API endpoints
 */
@Getter
@Setter
@Builder
public class ErrorResponseDTO {
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * Error type/category
     */
    private String error;
    
    /**
     * Main error message
     */
    private String message;
    
    /**
     * Detailed error description
     */
    private String details;
    
    /**
     * API endpoint that caused the error
     */
    private String path;
    
    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * Validation errors (field-specific errors)
     */
    private Map<String, String> validationErrors;
    
    /**
     * List of error messages (for multiple errors)
     */
    private List<String> errors;
    
    /**
     * Request ID for tracing (optional)
     */
    private String requestId;
}