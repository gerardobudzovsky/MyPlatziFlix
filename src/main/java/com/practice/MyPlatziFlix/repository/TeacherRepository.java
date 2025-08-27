package com.practice.MyPlatziFlix.repository;

import com.practice.MyPlatziFlix.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Teacher entity operations
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    
    /**
     * Find all teachers that are not soft deleted
     */
    @Query("SELECT t FROM Teacher t WHERE t.deletedAt IS NULL")
    List<Teacher> findAllActive();
    
    /**
     * Find teacher by id that is not soft deleted
     */
    @Query("SELECT t FROM Teacher t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Teacher> findActiveById(Long id);
    
    /**
     * Find teacher by email
     */
    Optional<Teacher> findByEmail(String email);
    
    /**
     * Find teacher by username
     */
    Optional<Teacher> findByUsername(String username);
    
    /**
     * Find all teachers including soft deleted ones
     */
    @Query("SELECT t FROM Teacher t")
    List<Teacher> findAllIncludingDeleted();
    
    /**
     * Find all soft deleted teachers
     */
    @Query("SELECT t FROM Teacher t WHERE t.deletedAt IS NOT NULL")
    List<Teacher> findAllDeleted();
    
    /**
     * Find soft deleted teacher by id
     */
    @Query("SELECT t FROM Teacher t WHERE t.id = :id AND t.deletedAt IS NOT NULL")
    Optional<Teacher> findDeletedById(Long id);
    
    /**
     * Find active teachers by specialization
     */
    @Query("SELECT t FROM Teacher t WHERE t.specialization LIKE %:specialization% AND t.deletedAt IS NULL")
    List<Teacher> findActiveBySpecializationContaining(String specialization);

} 