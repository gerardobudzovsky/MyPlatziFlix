package com.practice.MyPlatziFlix.exception;

/**
 * Exception thrown when data validation fails at the business logic level
 */
public class InvalidDataException extends RuntimeException {
    
    private final String field;
    private final Object value;
    
    public InvalidDataException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }
    
    public InvalidDataException(String field, Object value, String message) {
        super(String.format("Invalid data for field '%s' with value '%s': %s", field, value, message));
        this.field = field;
        this.value = value;
    }
    
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
}