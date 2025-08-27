package com.practice.MyPlatziFlix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a lesson within a course
 */
@Entity
@Table(name = "lessons")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Lesson {

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

    @NotBlank(message = "Lesson name is required")
    @Size(max = 100, message = "Lesson name must be less than 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @NotBlank(message = "Lesson slug is required")
    @Size(max = 150, message = "Slug must be less than 150 characters")
    @Column(name = "slug", nullable = false, length = 150)
    private String slug;

    @Size(max = 500, message = "Video URL must be less than 500 characters")
    @Column(name = "video_url", length = 500)
    private String videoUrl;

    // Many-to-one relationship with course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

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
} 