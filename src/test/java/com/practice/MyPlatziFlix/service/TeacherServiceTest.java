package com.practice.MyPlatziFlix.service;

import com.practice.MyPlatziFlix.dto.TeacherUpdateDTO;
import com.practice.MyPlatziFlix.entity.Teacher;
import com.practice.MyPlatziFlix.exception.TeacherAlreadyExistsException;
import com.practice.MyPlatziFlix.exception.TeacherNotFoundException;
import com.practice.MyPlatziFlix.mapper.TeacherMapper;
import com.practice.MyPlatziFlix.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher existing;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existing = new Teacher();
        existing.setId(1L);
        existing.setEmail("john.doe@example.com");
        existing.setUsername("johndoe");
        existing.setPassword("Secret123");
    }

    @Test
    void update_whenNotFound_throws() {
        when(teacherRepository.findActiveById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> teacherService.updateActiveTeacher(1L, new TeacherUpdateDTO()))
                .isInstanceOf(TeacherNotFoundException.class);
    }

    @Test
    void update_emailConflict_throws() {
        when(teacherRepository.findActiveById(1L)).thenReturn(Optional.of(existing));

        TeacherUpdateDTO dto = new TeacherUpdateDTO();
        dto.setEmail("taken@example.com");

        Teacher other = new Teacher();
        other.setId(2L);
        when(teacherRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> teacherService.updateActiveTeacher(1L, dto))
                .isInstanceOf(TeacherAlreadyExistsException.class)
                .hasMessageContaining("email");
    }

    @Test
    void update_usernameConflict_throws() {
        when(teacherRepository.findActiveById(1L)).thenReturn(Optional.of(existing));

        TeacherUpdateDTO dto = new TeacherUpdateDTO();
        dto.setUsername("taken");

        Teacher other = new Teacher();
        other.setId(3L);
        when(teacherRepository.findByUsername("taken")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> teacherService.updateActiveTeacher(1L, dto))
                .isInstanceOf(TeacherAlreadyExistsException.class)
                .hasMessageContaining("username");
    }

    @Test
    void update_passwordBlank_preservesOldPassword() {
        when(teacherRepository.findActiveById(1L)).thenReturn(Optional.of(existing));

        TeacherUpdateDTO dto = new TeacherUpdateDTO();
        dto.setPassword("   ");

        doAnswer(invocation -> {
            Teacher target = invocation.getArgument(1);
            // mapper leaves password null, we restore below
            return null;
        }).when(teacherMapper).updateExistingTeacherEntityFromUpdateTeacherDTO(any(), any());

        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

        Teacher result = teacherService.updateActiveTeacher(1L, dto);
        assertThat(result.getPassword()).isEqualTo("Secret123");
    }
}


