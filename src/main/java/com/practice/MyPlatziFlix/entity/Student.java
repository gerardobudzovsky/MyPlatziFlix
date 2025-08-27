package com.practice.MyPlatziFlix.entity;

import com.practice.MyPlatziFlix.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a student in the platform
 */
@Entity
@Table(name = "students")
@Getter
@Setter
public class Student extends User {

    // One-to-many relationship with enrollments
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student() {
        setRole(UserRole.STUDENT);
    }

    /**
     * Helper method to enroll in a course
     */
    public void enrollInCourse(Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(this);
        enrollment.setCourse(course);
        enrollments.add(enrollment);
        course.getEnrollments().add(enrollment);
    }
} 