package com.practice.MyPlatziFlix.exception;

/**
 * Exception thrown when a teacher is not found
 */
public class TeacherNotFoundException extends RuntimeException {
    
    private final Long teacherId;
    
    public TeacherNotFoundException(Long teacherId) {
        super(String.format("Teacher with ID %d not found", teacherId));
        this.teacherId = teacherId;
    }
    
    public TeacherNotFoundException(String message) {
        super(message);
        this.teacherId = null;
    }
    
    public TeacherNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.teacherId = null;
    }
    
    public Long getTeacherId() {
        return teacherId;
    }
}