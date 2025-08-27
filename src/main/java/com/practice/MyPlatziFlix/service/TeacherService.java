package com.practice.MyPlatziFlix.service;

import com.practice.MyPlatziFlix.dto.TeacherUpdateDTO;
import com.practice.MyPlatziFlix.entity.Teacher;
import com.practice.MyPlatziFlix.exception.TeacherAlreadyExistsException;
import com.practice.MyPlatziFlix.exception.TeacherNotFoundException;
import com.practice.MyPlatziFlix.mapper.TeacherMapper;
import com.practice.MyPlatziFlix.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Teacher business logic
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    /**
     * Get all active teachers
     */
    public List<Teacher> getAllActiveTeachers() {
        log.info("Retrieving all active teachers");
        List<Teacher> teachers = teacherRepository.findAllActive();
        log.info("Retrieved {} active teachers", teachers.size());
        return teachers;
    }

    /**
     * Get active teacher by id if not soft deleted
     * @throws TeacherNotFoundException if teacher not found or is soft deleted
     */
    public Teacher getActiveTeacherById(Long id) {
        log.info("Retrieving active teacher by id {}", id);
        return teacherRepository.findActiveById(id)
                .orElseThrow(() -> new TeacherNotFoundException(id));
    }
    
    /**
     * Get active teacher by id if not soft deleted (returns Optional for backward compatibility)
     */
    public Optional<Teacher> findActiveTeacherById(Long id) {
        log.info("Finding active teacher by id {}", id);
        return teacherRepository.findActiveById(id);
    }

    /**
     * Save a new teacher
     * @throws TeacherAlreadyExistsException if email or username already exists
     */
    @Transactional(readOnly = false)
    public Teacher saveTeacher(Teacher teacher) {
        log.info("Attempting to save new teacher with email '{}' and username '{}'", teacher.getEmail(), teacher.getUsername());
        // Check for existing email
        teacherRepository.findByEmail(teacher.getEmail()).ifPresent(existing -> {
            log.warn("Cannot save teacher. Email '{}' already exists (id={})", teacher.getEmail(), existing.getId());
            throw new TeacherAlreadyExistsException("email", teacher.getEmail());
        });

        // Check for existing username
        teacherRepository.findByUsername(teacher.getUsername()).ifPresent(existing -> {
            log.warn("Cannot save teacher. Username '{}' already exists (id={})", teacher.getUsername(), existing.getId());
            throw new TeacherAlreadyExistsException("username", teacher.getUsername());
        });

        Teacher saved = teacherRepository.save(teacher);
        log.info("Teacher saved successfully with id {}", saved.getId());
        return saved;
    }

    /**
     * Update an existing active teacher
     * @throws TeacherNotFoundException if teacher not found or is soft deleted
     * @throws TeacherAlreadyExistsException if email or username conflicts with another teacher
     */
    @Transactional(readOnly = false)
    public Teacher updateActiveTeacher(Long id, TeacherUpdateDTO teacherUpdateDTO) {
        log.info("Updating active teacher with id {}", id);
        Teacher existingActiveTeacher = teacherRepository.findActiveById(id)
                .orElseThrow(() -> new TeacherNotFoundException(id));
        // Validate email uniqueness if provided
        if (teacherUpdateDTO.getEmail() != null && !teacherUpdateDTO.getEmail().trim().isEmpty()) {
            teacherRepository.findByEmail(teacherUpdateDTO.getEmail())
                    .ifPresent(other -> {
                        if (!other.getId().equals(id)) {
                            log.warn("Cannot update teacher {}. Email '{}' conflicts with teacher {}", id, teacherUpdateDTO.getEmail(), other.getId());
                            throw new TeacherAlreadyExistsException("email", teacherUpdateDTO.getEmail());
                        }
                    });
        }

        // Validate username uniqueness if provided
        if (teacherUpdateDTO.getUsername() != null && !teacherUpdateDTO.getUsername().trim().isEmpty()) {
            teacherRepository.findByUsername(teacherUpdateDTO.getUsername())
                    .ifPresent(other -> {
                        if (!other.getId().equals(id)) {
                            log.warn("Cannot update teacher {}. Username '{}' conflicts with teacher {}", id, teacherUpdateDTO.getUsername(), other.getId());
                            throw new TeacherAlreadyExistsException("username", teacherUpdateDTO.getUsername());
                        }
                    });
        }
        // Store current password before mapping
        String currentPassword = existingActiveTeacher.getPassword();
        
        // Map DTO to entity
        teacherMapper.updateExistingTeacherEntityFromUpdateTeacherDTO(teacherUpdateDTO, existingActiveTeacher);
        
        // If password is null or empty, restore the original password
        if (teacherUpdateDTO.getPassword() == null || teacherUpdateDTO.getPassword().trim().isEmpty()) {
            existingActiveTeacher.setPassword(currentPassword);
        }
        
        Teacher saved = teacherRepository.save(existingActiveTeacher);
        log.info("Teacher {} updated successfully", id);
        return saved;
    }

    /**
     * Soft delete an active teacher
     * @throws TeacherNotFoundException if teacher not found or already soft deleted
     */
    @Transactional(readOnly = false)
    public void softDeleteTeacher(Long id) {
        log.info("Soft deleting teacher with id {}", id);
        Teacher teacher = teacherRepository.findActiveById(id)
                .orElseThrow(() -> new  TeacherNotFoundException(id));
        
        teacher.markAsDeleted();
        teacherRepository.save(teacher);
        log.info("Teacher with id {} soft deleted", id);
    }

    /**
     * Find teacher by username
     */
    public Optional<Teacher> findByUsername(String username) {
        log.info("Finding teacher by username '{}'", username);
        return teacherRepository.findByUsername(username);
    }
    
    /**
     * Get all teachers including soft deleted ones
     */
    public List<Teacher> getAllTeachersIncludingDeleted() {
        log.info("Retrieving all teachers including deleted");
        return teacherRepository.findAllIncludingDeleted();
    }
    
    /**
     * Get all soft deleted teachers
     */
    public List<Teacher> getAllDeletedTeachers() {
        log.info("Retrieving all soft deleted teachers");
        return teacherRepository.findAllDeleted();
    }
    
    /**
     * Get soft deleted teacher by id
     * @throws TeacherNotFoundException if teacher not found or is not soft deleted
     */
    public Teacher getSoftDeletedTeacherById(Long id) {
        log.info("Retrieving soft deleted teacher by id {}", id);
        return teacherRepository.findDeletedById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher with ID " + id + " not found in deleted records"));
    }
    
    /**
     * Find soft deleted teacher by id (returns Optional for backward compatibility)
     */
    public Optional<Teacher> findDeletedTeacherById(Long id) {
        log.info("Finding soft deleted teacher by id {}", id);
        return teacherRepository.findDeletedById(id);
    }
    
    /**
     * Restore a soft deleted teacher
     * @throws TeacherNotFoundException if teacher not found in deleted records
     */
    @Transactional(readOnly = false)
    public Teacher restoreSoftDeletedTeacher(Long id) {
        log.info("Restoring soft deleted teacher with id {}", id);
        Teacher teacher = teacherRepository.findDeletedById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher with ID " + id + " not found in deleted records"));
        
        teacher.setDeletedAt(null);
        Teacher restored = teacherRepository.save(teacher);
        log.info("Teacher with id {} restored successfully", id);
        return restored;
    }
    
    /**
     * Find active teachers by specialization
     */
    public List<Teacher> findActiveTeachersBySpecialization(String specialization) {
        log.info("Finding active teachers by specialization containing '{}'", specialization);
        return teacherRepository.findActiveBySpecializationContaining(specialization);
    }

} 