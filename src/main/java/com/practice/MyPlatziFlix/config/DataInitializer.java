package com.practice.MyPlatziFlix.config;

import com.practice.MyPlatziFlix.entity.Teacher;
import com.practice.MyPlatziFlix.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component that initializes the database with sample data when the application starts
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TeacherService teacherService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database with sample data...");
        
        // Check if teachers already exist to avoid duplicates
        if (teacherService.getAllActiveTeachers().isEmpty()) {
            createSampleTeachers();
            log.info("Sample teachers created successfully!");
        } else {
            log.info("Teachers already exist in database, skipping initialization.");
        }
    }

    private void createSampleTeachers() {
        // Teacher 1
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("John");
        teacher1.setLastName("Smith");
        teacher1.setEmail("john.smith@myplatziflix.com");
        teacher1.setUsername("johnsmith");
        teacher1.setPassword("password123");
        teacher1.setBiography("Experienced software engineer with 10+ years in web development. Passionate about teaching modern JavaScript frameworks and best practices.");
        teacher1.setSpecialization("JavaScript & React");
        teacherService.saveTeacher(teacher1);

        // Teacher 2
        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Maria");
        teacher2.setLastName("Rodriguez");
        teacher2.setEmail("maria.rodriguez@myplatziflix.com");
        teacher2.setUsername("mariarodriguez");
        teacher2.setPassword("password123");
        teacher2.setBiography("Full-stack developer and Python expert. Specialized in backend development, APIs, and data science. Love sharing knowledge about clean code and architecture patterns.");
        teacher2.setSpecialization("Python & Backend Development");
        teacherService.saveTeacher(teacher2);

        // Teacher 3
        Teacher teacher3 = new Teacher();
        teacher3.setFirstName("David");
        teacher3.setLastName("Chen");
        teacher3.setEmail("david.chen@myplatziflix.com");
        teacher3.setUsername("davidchen");
        teacher3.setPassword("password123");
        teacher3.setBiography("Mobile development specialist with expertise in iOS and Android platforms. Focused on creating intuitive user experiences and performance optimization.");
        teacher3.setSpecialization("Mobile Development");
        teacherService.saveTeacher(teacher3);

        log.info("Created 3 sample teachers: John Smith, Maria Rodriguez, and David Chen");
    }
} 