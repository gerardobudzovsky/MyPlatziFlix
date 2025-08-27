package com.practice.MyPlatziFlix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.MyPlatziFlix.dto.TeacherCreateDTO;
import com.practice.MyPlatziFlix.dto.TeacherResponseDTO;
import com.practice.MyPlatziFlix.dto.TeacherUpdateDTO;
import com.practice.MyPlatziFlix.entity.Teacher;
import com.practice.MyPlatziFlix.exception.GlobalExceptionHandler;
import com.practice.MyPlatziFlix.exception.TeacherAlreadyExistsException;
import com.practice.MyPlatziFlix.exception.TeacherNotFoundException;
import com.practice.MyPlatziFlix.mapper.TeacherMapper;
import com.practice.MyPlatziFlix.service.TeacherService;
import com.practice.MyPlatziFlix.repository.TeacherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TeacherController.class)
@Import(GlobalExceptionHandler.class)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private TeacherMapper teacherMapper;

    @MockitoBean
    private TeacherRepository teacherRepository;

    private Teacher mockTeacher(Long id) {
        Teacher t = new Teacher();
        t.setId(id);
        t.setFirstName("John");
        t.setLastName("Doe");
        t.setEmail("john.doe@example.com");
        t.setUsername("johndoe");
        t.setPassword("Secret123");
        return t;
    }

    private TeacherResponseDTO mockDTO() {
        TeacherResponseDTO dto = new TeacherResponseDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setUsername("johndoe");
        return dto;
    }

    @Test
    @DisplayName("GET /teachers returns 200 with list")
    void getAllActiveTeachers_ok() throws Exception {
        List<Teacher> teachers = List.of(mockTeacher(1L));
        Mockito.when(teacherService.getAllActiveTeachers()).thenReturn(teachers);
        Mockito.when(teacherMapper.convertFromTeacherEntityListToGetTeacherDTOList(anyList()))
                .thenReturn(List.of(mockDTO()));

        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /teachers/{id} returns 200 when found")
    void getActiveTeacherById_ok() throws Exception {
        Mockito.when(teacherService.getActiveTeacherById(1L)).thenReturn(mockTeacher(1L));
        Mockito.when(teacherMapper.convertFromTeacherEntityToGetTeacherDTO(any(Teacher.class)))
                .thenReturn(mockDTO());

        mockMvc.perform(get("/teachers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /teachers/{id} returns 404 when not found")
    void getActiveTeacherById_notFound() throws Exception {
        Mockito.when(teacherService.getActiveTeacherById(99L)).thenThrow(new TeacherNotFoundException(99L));

        mockMvc.perform(get("/teachers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /teachers returns 200 when created")
    void createTeacher_ok() throws Exception {
        TeacherCreateDTO createDTO = new TeacherCreateDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setEmail("john.doe@example.com");
        createDTO.setUsername("johndoe");
        createDTO.setPassword("Secret123");

        Mockito.when(teacherRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        Mockito.when(teacherRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        Mockito.when(teacherMapper.convertFromCreateDTOToTeacherEntity(any(TeacherCreateDTO.class)))
                .thenReturn(mockTeacher(null));
        Mockito.when(teacherService.saveTeacher(any(Teacher.class))).thenReturn(mockTeacher(1L));
        Mockito.when(teacherMapper.convertFromTeacherEntityToGetTeacherDTO(any(Teacher.class)))
                .thenReturn(mockDTO());

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /teachers returns 409 on duplicate email/username")
    void createTeacher_conflict() throws Exception {
        TeacherCreateDTO createDTO = new TeacherCreateDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setEmail("john.doe@example.com");
        createDTO.setUsername("johndoe");
        createDTO.setPassword("Secret123");

        Mockito.when(teacherRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        Mockito.when(teacherRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        Mockito.when(teacherMapper.convertFromCreateDTOToTeacherEntity(any(TeacherCreateDTO.class)))
                .thenReturn(mockTeacher(null));
        Mockito.when(teacherService.saveTeacher(any(Teacher.class)))
                .thenThrow(new TeacherAlreadyExistsException("email", "john.doe@example.com"));

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT/PATCH /teachers/{id} returns 200 when updated")
    void updateTeacher_ok() throws Exception {
        TeacherUpdateDTO updateDTO = new TeacherUpdateDTO();
        updateDTO.setFirstName("Johnny");
        updateDTO.setBiography("Bio updated lorem ipsum");

        Mockito.when(teacherService.updateActiveTeacher(eq(1L), any(TeacherUpdateDTO.class)))
                .thenReturn(mockTeacher(1L));
        Mockito.when(teacherMapper.convertFromTeacherEntityToGetTeacherDTO(any(Teacher.class)))
                .thenReturn(mockDTO());

        mockMvc.perform(put("/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(patch("/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /teachers/{id} returns 409 when email conflict")
    void updateTeacher_conflict() throws Exception {
        TeacherUpdateDTO updateDTO = new TeacherUpdateDTO();
        updateDTO.setEmail("taken@example.com");

        Mockito.when(teacherService.updateActiveTeacher(eq(1L), any(TeacherUpdateDTO.class)))
                .thenThrow(new TeacherAlreadyExistsException("email", "taken@example.com"));

        mockMvc.perform(put("/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /teachers/{id} returns 404 when not found")
    void updateTeacher_notFound() throws Exception {
        TeacherUpdateDTO updateDTO = new TeacherUpdateDTO();
        updateDTO.setBiography("Bio updated lorem ipsum");

        Mockito.when(teacherService.updateActiveTeacher(eq(1L), any(TeacherUpdateDTO.class)))
                .thenThrow(new TeacherNotFoundException(1L));

        mockMvc.perform(put("/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /teachers/{id} returns 204")
    void softDeleteTeacher_noContent() throws Exception {
        mockMvc.perform(delete("/teachers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /teachers/all returns 200")
    void getAllIncludingDeleted_ok() throws Exception {
        Mockito.when(teacherService.getAllTeachersIncludingDeleted()).thenReturn(List.of(mockTeacher(1L)));
        Mockito.when(teacherMapper.convertFromTeacherEntityListToGetTeacherDTOList(anyList()))
                .thenReturn(List.of(mockDTO()));

        mockMvc.perform(get("/teachers/all"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /teachers/deleted returns 200")
    void getAllDeleted_ok() throws Exception {
        Mockito.when(teacherService.getAllDeletedTeachers()).thenReturn(List.of(mockTeacher(2L)));
        Mockito.when(teacherMapper.convertFromTeacherEntityListToGetTeacherDTOList(anyList()))
                .thenReturn(List.of(mockDTO()));

        mockMvc.perform(get("/teachers/deleted"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /teachers/deleted/{id} returns 200 when found")
    void getDeletedById_ok() throws Exception {
        Mockito.when(teacherService.getSoftDeletedTeacherById(2L)).thenReturn(mockTeacher(2L));
        Mockito.when(teacherMapper.convertFromTeacherEntityToGetTeacherDTO(any(Teacher.class)))
                .thenReturn(mockDTO());

        mockMvc.perform(get("/teachers/deleted/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /teachers/deleted/{id} returns 404 when not found")
    void getDeletedById_notFound() throws Exception {
        Mockito.when(teacherService.getSoftDeletedTeacherById(3L))
                .thenThrow(new TeacherNotFoundException("not found"));

        mockMvc.perform(get("/teachers/deleted/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /teachers/{id}/restore returns 200")
    void restore_ok() throws Exception {
        Mockito.when(teacherService.restoreSoftDeletedTeacher(5L)).thenReturn(mockTeacher(5L));
        Mockito.when(teacherMapper.convertFromTeacherEntityToGetTeacherDTO(any(Teacher.class)))
                .thenReturn(mockDTO());

        mockMvc.perform(post("/teachers/5/restore"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /teachers/search/specialization returns 200")
    void searchSpecialization_ok() throws Exception {
        Mockito.when(teacherService.findActiveTeachersBySpecialization("java"))
                .thenReturn(List.of(mockTeacher(10L)));
        Mockito.when(teacherMapper.convertFromTeacherEntityListToGetTeacherDTOList(anyList()))
                .thenReturn(List.of(mockDTO()));

        mockMvc.perform(get("/teachers/search/specialization").param("q", "java"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /teachers/search/username returns 200 when found and 404 when not")
    void searchByUsername() throws Exception {
        Mockito.when(teacherService.findByUsername("john"))
                .thenReturn(Optional.of(mockTeacher(1L)));
        Mockito.when(teacherMapper.convertFromTeacherEntityToGetTeacherDTO(any(Teacher.class)))
                .thenReturn(mockDTO());

        mockMvc.perform(get("/teachers/search/username").param("q", "john"))
                .andExpect(status().isOk());

        Mockito.when(teacherService.findByUsername("missing")).thenReturn(Optional.empty());
        mockMvc.perform(get("/teachers/search/username").param("q", "missing"))
                .andExpect(status().isNotFound());
    }
}


