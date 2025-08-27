package com.practice.MyPlatziFlix.exception;

/**
 * Exception thrown when trying to access a soft-deleted (inactive) teacher
 */
public class TeacherInactiveException extends RuntimeException {
    
    private final Long teacherId;
    
    public TeacherInactiveException(Long teacherId) {
        super(String.format("Teacher with ID %d is inactive (soft deleted)", teacherId));
        this.teacherId = teacherId;
    }
    
    public TeacherInactiveException(String message) {
        super(message);
        this.teacherId = null;
    }
    
    public TeacherInactiveException(String message, Throwable cause) {
        super(message, cause);
        this.teacherId = null;
    }
    
    public Long getTeacherId() {
        return teacherId;
    }
}