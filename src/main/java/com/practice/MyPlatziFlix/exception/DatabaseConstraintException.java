package com.practice.MyPlatziFlix.exception;

/**
 * Exception thrown when database constraints are violated
 */
public class DatabaseConstraintException extends RuntimeException {
    
    private final String constraintName;
    private final String tableName;
    
    public DatabaseConstraintException(String message) {
        super(message);
        this.constraintName = null;
        this.tableName = null;
    }
    
    public DatabaseConstraintException(String constraintName, String tableName, String message) {
        super(String.format("Database constraint '%s' violated in table '%s': %s", constraintName, tableName, message));
        this.constraintName = constraintName;
        this.tableName = tableName;
    }
    
    public DatabaseConstraintException(String message, Throwable cause) {
        super(message, cause);
        this.constraintName = null;
        this.tableName = null;
    }
    
    public String getConstraintName() {
        return constraintName;
    }
    
    public String getTableName() {
        return tableName;
    }
}