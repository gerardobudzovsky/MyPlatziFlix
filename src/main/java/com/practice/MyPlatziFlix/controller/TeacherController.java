package com.practice.MyPlatziFlix.controller;

import com.practice.MyPlatziFlix.dto.TeacherCreateDTO;
import com.practice.MyPlatziFlix.dto.TeacherUpdateDTO;
import com.practice.MyPlatziFlix.dto.TeacherResponseDTO;
import com.practice.MyPlatziFlix.entity.Teacher;
import com.practice.MyPlatziFlix.exception.TeacherNotFoundException;
import com.practice.MyPlatziFlix.mapper.TeacherMapper;
import com.practice.MyPlatziFlix.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
 

/**
 * REST controller for Teacher operations
 */
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Operations related to teachers")
@Slf4j
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherMapper teacherMapper;

    /**
     * Get all active teachers
     * GET /api/teachers
     */
    @GetMapping
    @Operation(summary = "Get all active teachers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of active teachers",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TeacherResponseDTO.class))))
    })
    public ResponseEntity<List<TeacherResponseDTO>> getAllActiveTeachers() {
        log.info("Request to get all active teachers");
        List<Teacher> teachers = teacherService.getAllActiveTeachers();
        List<TeacherResponseDTO> teacherResponseDTOS = teacherMapper.convertFromTeacherEntityListToGetTeacherDTOList(teachers);
        log.info("Returning {} active teachers", teacherResponseDTOS.size());
        return ResponseEntity.ok(teacherResponseDTOS);
    }

    /**
     * Get active teacher by ID
     * GET /api/teachers/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get active teacher by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeacherResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Teacher not found",
                    content = @Content)
    })
    public ResponseEntity<TeacherResponseDTO> getActiveTeacherById(
            @Parameter(description = "Teacher ID", required = true) @PathVariable Long id) {
        log.info("Request to get active teacher by id {}", id);
        Teacher teacher = teacherService.getActiveTeacherById(id);
        TeacherResponseDTO teacherDTO = teacherMapper.convertFromTeacherEntityToGetTeacherDTO(teacher);
        return ResponseEntity.ok(teacherDTO);
    }

    /**
     * Create a new teacher
     * POST /api/teachers
     */
    @PostMapping
    @Operation(summary = "Create a new teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeacherResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email or username already exists", content = @Content)
    })
    public ResponseEntity<TeacherResponseDTO> createTeacher(@Valid @RequestBody TeacherCreateDTO teacherCreateDTO) {
        log.info("Request to create teacher with email '{}' and username '{}'", teacherCreateDTO.getEmail(), teacherCreateDTO.getUsername());
        Teacher teacher = teacherMapper.convertFromCreateDTOToTeacherEntity(teacherCreateDTO);
        Teacher savedTeacher = teacherService.saveTeacher(teacher);
        TeacherResponseDTO responseDTO = teacherMapper.convertFromTeacherEntityToGetTeacherDTO(savedTeacher);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Update an existing active teacher
     * PUT /api/teachers/{id}
     * PATCH /api/teachers/{id}
     */
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    @Operation(summary = "Update an existing active teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeacherResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email or username conflict", content = @Content)
    })
    public ResponseEntity<TeacherResponseDTO> updateActiveTeacher(
            @Parameter(description = "Teacher ID", required = true) @PathVariable Long id,
            @Valid @RequestBody TeacherUpdateDTO teacherDTO) {
        log.info("Request to update active teacher with id {}", id);
        Teacher updatedTeacher = teacherService.updateActiveTeacher(id, teacherDTO);
        TeacherResponseDTO responseDTO = teacherMapper.convertFromTeacherEntityToGetTeacherDTO(updatedTeacher);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Soft delete an active teacher
     * DELETE /api/teachers/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete an active teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Teacher soft deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content)
    })
    public ResponseEntity<Void> softDeleteTeacher(@Parameter(description = "Teacher ID", required = true) @PathVariable Long id) {
        log.info("Request to soft delete teacher with id {}", id);
        teacherService.softDeleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all teachers including soft deleted ones
     * GET /api/teachers/all
     */
    @GetMapping("/all")
    @Operation(summary = "Get all teachers including soft deleted ones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of teachers",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TeacherResponseDTO.class))))
    })
    public ResponseEntity<List<TeacherResponseDTO>> getAllTeachersIncludingDeleted() {
        log.info("Request to get all teachers including deleted");
        List<Teacher> teachers = teacherService.getAllTeachersIncludingDeleted();
        List<TeacherResponseDTO> teacherResponseDTOS = teacherMapper.convertFromTeacherEntityListToGetTeacherDTOList(teachers);
        return ResponseEntity.ok(teacherResponseDTOS);
    }

    /**
     * Get all soft deleted teachers
     * GET /api/teachers/deleted
     */
    @GetMapping("/deleted")
    @Operation(summary = "Get all soft deleted teachers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of soft deleted teachers",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TeacherResponseDTO.class))))
    })
    public ResponseEntity<List<TeacherResponseDTO>> getAllDeletedTeachers() {
        log.info("Request to get all soft deleted teachers");
        List<Teacher> deletedTeachers = teacherService.getAllDeletedTeachers();
        List<TeacherResponseDTO> teacherResponseDTOS = teacherMapper.convertFromTeacherEntityListToGetTeacherDTOList(deletedTeachers);
        return ResponseEntity.ok(teacherResponseDTOS);
    }

    /**
     * Get soft deleted teacher by id
     * GET /api/teachers/deleted/{id}
     */
        @GetMapping("/deleted/{id}")
    @Operation(summary = "Get soft deleted teacher by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Soft deleted teacher found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeacherResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Deleted teacher not found",
                    content = @Content)
    })
    public ResponseEntity<TeacherResponseDTO> getSoftDeletedTeacherById(@Parameter(description = "Teacher ID", required = true) @PathVariable Long id) {
        log.info("Request to get soft deleted teacher by id {}", id);
        Teacher teacher = teacherService.getSoftDeletedTeacherById(id);
        TeacherResponseDTO teacherDTO = teacherMapper.convertFromTeacherEntityToGetTeacherDTO(teacher);
        return ResponseEntity.ok(teacherDTO);
    }

    /**
     * Restore a soft-deleted teacher
     * POST /api/teachers/{id}/restore
     */
    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore a soft-deleted teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher restored",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeacherResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Deleted teacher not found",
                    content = @Content)
    })
    public ResponseEntity<TeacherResponseDTO> restoreSoftDeletedTeacher(@Parameter(description = "Teacher ID", required = true) @PathVariable Long id) {
        log.info("Request to restore soft deleted teacher with id {}", id);
        Teacher restoredTeacher = teacherService.restoreSoftDeletedTeacher(id);
        TeacherResponseDTO teacherDTO = teacherMapper.convertFromTeacherEntityToGetTeacherDTO(restoredTeacher);
        return ResponseEntity.ok(teacherDTO);
    }

    /**
     * Search active teachers by specialization
     * GET /api/teachers/search/specialization?q={specialization}
     */
    @GetMapping("/search/specialization")
    @Operation(summary = "Search active teachers by specialization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active teachers matching specialization",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TeacherResponseDTO.class))))
    })
    public ResponseEntity<List<TeacherResponseDTO>> findActiveTeachersBySpecialization(
            @Parameter(description = "Specialization query", required = true)
            @RequestParam("q") String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            log.warn("Specialization query parameter is blank");
        }
        log.info("Searching active teachers by specialization '{}'", specialization);
        List<Teacher> teachers = teacherService.findActiveTeachersBySpecialization(specialization);
        List<TeacherResponseDTO> teacherResponseDTOS = teacherMapper.convertFromTeacherEntityListToGetTeacherDTOList(teachers);
        return ResponseEntity.ok(teacherResponseDTOS);
    }

    /**
     * Find active teacher by username
     * GET /api/teachers/search/username?q={username}
     */
    @GetMapping("/search/username")
    @Operation(summary = "Find active teacher by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeacherResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Teacher not found",
                    content = @Content)
    })
    public ResponseEntity<TeacherResponseDTO> findActiveTeacherByUsername(
            @Parameter(description = "Username query", required = true)
            @RequestParam("q") String username) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("Username query parameter is blank");
        }
        log.info("Finding active teacher by username '{}'", username);
        Teacher teacher = teacherService.findByUsername(username)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new TeacherNotFoundException("Active teacher with username '" + username + "' not found"));
        
        TeacherResponseDTO teacherDTO = teacherMapper.convertFromTeacherEntityToGetTeacherDTO(teacher);
            return ResponseEntity.ok(teacherDTO);
    }
} 