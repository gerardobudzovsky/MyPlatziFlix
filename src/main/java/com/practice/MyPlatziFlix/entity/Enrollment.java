package com.practice.MyPlatziFlix.entity;

import com.practice.MyPlatziFlix.enums.EnrollmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing the enrollment of a student in a course
 */
@Entity
@Table(name = "enrollments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull(message = "Enrollment date is required")
    @Column(name = "enrollment_date_time", nullable = false)
    private LocalDateTime enrollmentDateTime;

    @Column(name = "completion_date_time")
    private LocalDateTime completionDateTime;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    // Many-to-one relationship with student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Many-to-one relationship with course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Enrollment() {
        this.enrollmentDateTime = LocalDateTime.now();
        this.status = EnrollmentStatus.ACTIVE;
    }

    /**
     * Marks this entity as deleted by setting the deletedAt timestamp
     */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Checks if this entity is soft deleted
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Marks the enrollment as completed
     */
    public void markAsCompleted() {
        this.status = EnrollmentStatus.COMPLETED;
        this.completionDateTime = LocalDateTime.now();
    }

    /**
     * Cancels the enrollment
     */
    public void cancel() {
        this.status = EnrollmentStatus.CANCELLED;
    }

    /**
     * Suspends the enrollment
     */
    public void suspend() {
        this.status = EnrollmentStatus.SUSPENDED;
    }
} 