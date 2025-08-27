package com.practice.MyPlatziFlix.mapper;

import com.practice.MyPlatziFlix.dto.TeacherCreateDTO;
import com.practice.MyPlatziFlix.dto.TeacherUpdateDTO;
import com.practice.MyPlatziFlix.dto.TeacherResponseDTO;
import com.practice.MyPlatziFlix.entity.Course;
import com.practice.MyPlatziFlix.entity.Teacher;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Teacher entity and DTOs.
 * This mapper handles:
 * - Converting Teacher entity to TeacherResponseDTO for API responses
 * - Converting TeacherCreationAndUpdateDTO to Teacher entity for creation
 * - Updating existing Teacher entity with TeacherCreationAndUpdateDTO for updates
 * - Custom mapping for courses to a slug-name map for API responses
 * - Handling password updates only if provided for updates
 *
 */
@Mapper(componentModel = "spring")
public interface TeacherMapper {

    /**
     * Convert Teacher entity to TeacherResponseDTO for API responses.
     * Maps courses to a slug-name map for easier API consumption.
     */
    @Mapping(target = "courses", source = "courses", qualifiedByName = "coursesToSlugNameMap")
    TeacherResponseDTO convertFromTeacherEntityToGetTeacherDTO(Teacher teacher);

    /**
     * Convert list of Teacher entities to list of GetTeacherDTOs for API responses.
     */
    List<TeacherResponseDTO> convertFromTeacherEntityListToGetTeacherDTOList(List<Teacher> teachers);

    /**
     * Convert TeacherCreateDTO to Teacher entity for creation
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Teacher convertFromCreateDTOToTeacherEntity(TeacherCreateDTO dto);

    /**
     * Update existing Teacher entity with data from TeacherUpdateDTO
     * Ignores fields that should not be updated (id, createdAt, updatedAt, deletedAt, role, courses)
     * Custom mapping for password to only update if provided
     * @param teacherUpdateDTO DTO containing the data to update
     * @param existingTeacherEntity The existing Teacher entity to update
     * @return void - the existingTeacherEntity is updated in place
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "password", source = "password", qualifiedByName = "updatePassword")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExistingTeacherEntityFromUpdateTeacherDTO(TeacherUpdateDTO teacherUpdateDTO, @MappingTarget Teacher existingTeacherEntity);

    /**
     * Custom mapping for password - only update if not null or empty
     */
    @Named("updatePassword")
    default String updatePassword(String password) {
        return (password != null && !password.trim().isEmpty()) ? password : null;
    }

    /**
     * Convert Teacher entity courses to slug-name map
     */
    @Named("coursesToSlugNameMap")
    default Map<String, String> coursesToSlugNameMap(List<Course> courses) {
        if (courses == null) {
            return null;
        }
        return courses.stream()
                .collect(Collectors.toMap(
                    Course::getSlug,
                    Course::getName
                ));
    }
}