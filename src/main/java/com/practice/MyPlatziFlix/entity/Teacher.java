package com.practice.MyPlatziFlix.entity;

import com.practice.MyPlatziFlix.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a teacher in the platform
 */
@Entity
@Table(name = "teachers")
@Getter
@Setter
public class Teacher extends User {

    @Size(min = 10, max = 2000, message = "Biography must be between 10 and 2000 characters when provided")
    @Column(name = "biography", length = 2000)
    private String biography;

    @Pattern(regexp = "^[a-zA-Z0-9\\s&+.-]+$", message = "Specialization can only contain letters, numbers, spaces, and common symbols (&, +, ., -)")
    @Size(min = 3, max = 100, message = "Specialization must be between 3 and 100 characters when provided")
    @Column(name = "specialization", length = 100)
    private String specialization;

    // Many-to-many relationship with courses (inverse side)
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    public Teacher() {
        setRole(UserRole.TEACHER);
    }

    /**
     * Helper method to add a course to this teacher
     */
    public void addCourse(Course course) {
        courses.add(course);
        course.getTeachers().add(this);
    }

    /**
     * Helper method to remove a course from this teacher
     */
    public void removeCourse(Course course) {
        courses.remove(course);
        course.getTeachers().remove(this);
    }
} 