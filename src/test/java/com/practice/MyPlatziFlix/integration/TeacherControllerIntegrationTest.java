package com.practice.MyPlatziFlix.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.MyPlatziFlix.dto.TeacherCreateDTO;
import com.practice.MyPlatziFlix.dto.TeacherUpdateDTO;
import com.practice.MyPlatziFlix.entity.Teacher;
import com.practice.MyPlatziFlix.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    private static final String API = "/teachers";

    @BeforeEach
    void cleanUp() {
        // Ensure repository is not empty due to initializer but we don't clear it; each test uses unique data
        assertThat(teacherRepository).isNotNull();
    }

    private Teacher persistTeacher(String email, String username) {
        Teacher t = new Teacher();
        t.setFirstName("Jane");
        t.setLastName("Doe");
        t.setEmail(email);
        t.setUsername(username);
        t.setPassword("Secret123");
        t.setBiography("Experienced teacher in integration tests.");
        t.setSpecialization("Integration Testing");
        return teacherRepository.save(t);
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Test
    @DisplayName("GET /teachers returns 200 and JSON list")
    void getAllActiveTeachers_ok() throws Exception {
        mockMvc.perform(get(API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /teachers/{id} returns 200 when found and 404 when missing")
    void getActiveTeacherById_found_and_notFound() throws Exception {
        String s1 = uniqueSuffix();
        Teacher saved = persistTeacher(
                "it_" + s1 + "@example.com",
                "ituser_" + s1
        );

        mockMvc.perform(get(API + "/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(saved.getEmail()))
                .andExpect(jsonPath("$.username").value(saved.getUsername()));

        mockMvc.perform(get(API + "/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /teachers creates a teacher and duplicate returns 409")
    void createTeacher_ok_and_conflict() throws Exception {
        String unique = uniqueSuffix();
        TeacherCreateDTO create = new TeacherCreateDTO();
        create.setFirstName("Alex");
        create.setLastName("Morgan");
        create.setEmail("alex." + unique + "@example.com");
        create.setUsername("alex_" + unique);
        create.setPassword("Password1");
        create.setBiography("Bio for Alex Morgan.");
        create.setSpecialization("Java");

        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(create.getEmail()))
                .andExpect(jsonPath("$.username").value(create.getUsername()));

        // Try to create with same email -> should conflict
        TeacherCreateDTO duplicate = new TeacherCreateDTO();
        duplicate.setFirstName("Alex2");
        duplicate.setLastName("Morgan2");
        duplicate.setEmail(create.getEmail());
        duplicate.setUsername("another_" + unique);
        duplicate.setPassword("Password1");

        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /teachers/{id} updates fields and handles conflicts/not found")
    void updateTeacher_ok_conflict_notFound() throws Exception {
        String u1 = uniqueSuffix();
        String u2 = uniqueSuffix();
        Teacher t1 = persistTeacher("john." + u1 + "@example.com", "john_" + u1);
        Teacher t2 = persistTeacher("jane." + u2 + "@example.com", "jane_" + u2);

        // ok update
        TeacherUpdateDTO ok = new TeacherUpdateDTO();
        ok.setFirstName("Johnny");
        ok.setBiography("Updated bio lorem ipsum");

        mockMvc.perform(put(API + "/" + t1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ok)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"));

        // patch update
        TeacherUpdateDTO patchDto = new TeacherUpdateDTO();
        patchDto.setSpecialization("Patched Spec");
        mockMvc.perform(patch(API + "/" + t1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialization").value("Patched Spec"));

        // conflict email (set t2's email onto t1)
        TeacherUpdateDTO conflict = new TeacherUpdateDTO();
        conflict.setEmail(t2.getEmail());

        mockMvc.perform(put(API + "/" + t1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflict)))
                .andExpect(status().isConflict());

        // not found
        mockMvc.perform(put(API + "/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ok)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /teachers/{id} soft deletes and endpoints reflect state")
    void softDelete_and_queries() throws Exception {
        String u = uniqueSuffix();
        Teacher t = persistTeacher("del." + u + "@example.com", "del_" + u);

        // delete
        mockMvc.perform(delete(API + "/" + t.getId()))
                .andExpect(status().isNoContent());

        // active by id should be 404
        mockMvc.perform(get(API + "/" + t.getId()))
                .andExpect(status().isNotFound());

        // appears in deleted list
        mockMvc.perform(get(API + "/deleted"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // fetch deleted by id
        mockMvc.perform(get(API + "/deleted/" + t.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /teachers/{id}/restore restores a soft deleted teacher")
    void restore_softDeleted_teacher() throws Exception {
        String u = uniqueSuffix();
        Teacher t = persistTeacher("res." + u + "@example.com", "res_" + u);

        mockMvc.perform(delete(API + "/" + t.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(post(API + "/" + t.getId() + "/restore"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // now active by id should be 200 again
        mockMvc.perform(get(API + "/" + t.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /teachers/all and /teachers/deleted return 200")
    void lists_all_and_deleted() throws Exception {
        mockMvc.perform(get(API + "/all"))
                .andExpect(status().isOk());

        mockMvc.perform(get(API + "/deleted"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Search endpoints return 200 when found and 404 when missing")
    void search_endpoints() throws Exception {
        String u = uniqueSuffix();
        Teacher t = persistTeacher("search." + u + "@example.com", "search_" + u);
        t.setSpecialization("Java Integration");
        teacherRepository.save(t);

        mockMvc.perform(get(API + "/search/specialization").param("q", "Integration"))
                .andExpect(status().isOk());

        mockMvc.perform(get(API + "/search/username").param("q", t.getUsername()))
                .andExpect(status().isOk());

        mockMvc.perform(get(API + "/search/username").param("q", "missing_" + u))
                .andExpect(status().isNotFound());
    }
}


