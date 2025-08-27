package com.practice.MyPlatziFlix.exception;

/**
 * Exception thrown when trying to create a teacher that already exists
 */
public class TeacherAlreadyExistsException extends RuntimeException {
    
    private final String field;
    private final String value;
    
    public TeacherAlreadyExistsException(String field, String value) {
        super(String.format("Teacher with %s '%s' already exists", field, value));
        this.field = field;
        this.value = value;
    }
    
    public TeacherAlreadyExistsException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }
    
    public TeacherAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
    }
    
    public String getField() {
        return field;
    }
    
    public String getValue() {
        return value;
    }
}