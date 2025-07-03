package com.example.lms.integration.course;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.lms.course.dto.CourseRequestDto;
import com.example.lms.course.dto.CourseSettingsRequestDto;
import com.example.lms.course.repository.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("local")
@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CourseRepository courseRepository;

  @BeforeEach
  void setUp() {
    courseRepository.deleteAll();
  }

  private String basicAuthHeader() {
    String plainCreds = "user:password";
    byte[] base64CredsBytes = java.util.Base64.getEncoder().encode(plainCreds.getBytes());
    return "Basic " + new String(base64CredsBytes);
  }

  private CourseRequestDto validCourseRequest() {
    return new CourseRequestDto(
        "Test Course",
        "A test course",
        BigDecimal.valueOf(100),
        BigDecimal.valueOf(10),
        new CourseSettingsRequestDto(
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(10),
            true
        )
    );
  }

  @Test
  void createCourse_shouldReturnCreatedAndCourseResponse() throws Exception {
    var request = validCourseRequest();

    mockMvc.perform(post("/api/v1/courses")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.title").value("Test Course"))
        .andExpect(jsonPath("$.description").value("A test course"))
        .andExpect(jsonPath("$.price").value(100))
        .andExpect(jsonPath("$.coinsPaid").value(10))
        .andExpect(jsonPath("$.settings").exists());
  }

  @Test
  void createCourse_shouldReturnBadRequestOnInvalidData() throws Exception {
    var invalidRequest = new CourseRequestDto(
        "",
        "desc",
        BigDecimal.valueOf(-1),
        BigDecimal.valueOf(-5),
        new CourseSettingsRequestDto(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(2),
            null
        )
    );

    mockMvc.perform(post("/api/v1/courses")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", containsString("Validation failed")))
        .andExpect(jsonPath("$.errors.title").exists())
        .andExpect(jsonPath("$.errors.price").exists())
        .andExpect(jsonPath("$.errors.coinsPaid").exists())
        .andExpect(jsonPath("$.errors['settings.startDate']").exists())
        .andExpect(jsonPath("$.errors['settings.endDate']").exists())
        .andExpect(jsonPath("$.errors['settings.isPublic']").exists());
  }

  @Test
  void getAllCourses_shouldReturnEmptyListInitially() throws Exception {
    mockMvc.perform(get("/api/v1/courses")
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  void getAllCourses_shouldReturnListOfCourses() throws Exception {
    var request = validCourseRequest();
    mockMvc.perform(post("/api/v1/courses")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/v1/courses")
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].title").value("Test Course"));
  }

  @Test
  void updateCourse_shouldUpdateAndReturnCourse() throws Exception {
    var request = validCourseRequest();
    String response = mockMvc.perform(post("/api/v1/courses")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    var updateRequest = new CourseRequestDto(
        "Updated Title",
        "Updated Desc",
        BigDecimal.valueOf(200),
        BigDecimal.valueOf(20),
        new CourseSettingsRequestDto(
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(20),
            false
        )
    );

    mockMvc.perform(put("/api/v1/courses/{id}", id)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"))
        .andExpect(jsonPath("$.description").value("Updated Desc"))
        .andExpect(jsonPath("$.price").value(200))
        .andExpect(jsonPath("$.coinsPaid").value(20))
        .andExpect(jsonPath("$.settings.isPublic").value(false));
  }

  @Test
  void updateCourse_shouldReturnNotFoundForInvalidId() throws Exception {
    UUID randomId = UUID.randomUUID();
    var updateRequest = validCourseRequest();

    mockMvc.perform(put("/api/v1/courses/{id}", randomId)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Course not found")));
  }

  @Test
  void updateCourse_shouldReturnBadRequestOnInvalidData() throws Exception {
    var request = validCourseRequest();
    String response = mockMvc.perform(post("/api/v1/courses")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    var invalidUpdate = new CourseRequestDto(
        "",
        "desc",
        BigDecimal.valueOf(-1),
        BigDecimal.valueOf(-5),
        new CourseSettingsRequestDto(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(2),
            null
        )
    );

    mockMvc.perform(put("/api/v1/courses/{id}", id)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUpdate)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", containsString("Validation failed")))
        .andExpect(jsonPath("$.errors.title").exists())
        .andExpect(jsonPath("$.errors.price").exists())
        .andExpect(jsonPath("$.errors.coinsPaid").exists())
        .andExpect(jsonPath("$.errors['settings.startDate']").exists())
        .andExpect(jsonPath("$.errors['settings.endDate']").exists())
        .andExpect(jsonPath("$.errors['settings.isPublic']").exists());
  }

  @Test
  void deleteCourse_shouldDeleteAndReturnNoContent() throws Exception {
    var request = validCourseRequest();
    String response = mockMvc.perform(post("/api/v1/courses")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    mockMvc.perform(delete("/api/v1/courses/{id}", id)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/courses")
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  void deleteCourse_shouldReturnNotFoundForInvalidId() throws Exception {
    UUID randomId = UUID.randomUUID();
    mockMvc.perform(delete("/api/v1/courses/{id}", randomId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Course not found")));
  }
}
