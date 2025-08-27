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
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a course in the platform
 */
@Entity
@Table(name = "courses")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Course {

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

    @NotBlank(message = "Course name is required")
    @Size(max = 100, message = "Course name must be less than 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Size(max = 500, message = "Thumbnail URL must be less than 500 characters")
    @Column(name = "thumbnail", length = 500)
    private String thumbnail;

    @NotBlank(message = "Course slug is required")
    @Size(max = 150, message = "Slug must be less than 150 characters")
    @Column(name = "slug", nullable = false, unique = true, length = 150)
    private String slug;

    // One-to-many relationship with lessons
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lesson> lessons = new ArrayList<>();

    // Many-to-many relationship with teachers
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_teachers",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private List<Teacher> teachers = new ArrayList<>();

    // One-to-many relationship with enrollments
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

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
     * Helper method to add a lesson to this course
     */
    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        lesson.setCourse(this);
    }

    /**
     * Helper method to remove a lesson from this course
     */
    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
        lesson.setCourse(null);
    }

    /**
     * Helper method to add a teacher to this course
     */
    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
        teacher.getCourses().add(this);
    }

    /**
     * Helper method to remove a teacher from this course
     */
    public void removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
        teacher.getCourses().remove(this);
    }
} 