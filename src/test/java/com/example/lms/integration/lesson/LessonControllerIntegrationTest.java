package com.example.lms.integration.lesson;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.lms.course.model.Course;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.lesson.dto.LessonRequestDto;
import com.example.lms.lesson.repository.LessonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class LessonControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private LessonRepository lessonRepository;

  @Autowired
  private CourseRepository courseRepository;

  private UUID courseId;

  private String basicAuthHeader() {
    String plainCreds = "user:password";
    byte[] base64CredsBytes = java.util.Base64.getEncoder().encode(plainCreds.getBytes());
    return "Basic " + new String(base64CredsBytes);
  }

  @BeforeEach
  void setUp() {
    lessonRepository.deleteAll();
    courseRepository.deleteAll();
    Course course = new Course();
    course.setTitle("Course for Lesson");
    course.setDescription("desc");
    course.setPrice(java.math.BigDecimal.TEN);
    course.setCoinsPaid(java.math.BigDecimal.ONE);
    com.example.lms.course.model.CourseSettings settings = new com.example.lms.course.model.CourseSettings();
    settings.setStartDate(java.time.LocalDateTime.now().plusDays(1));
    settings.setEndDate(java.time.LocalDateTime.now().plusDays(10));
    settings.setIsPublic(true);
    course.setSettings(settings);
    course = courseRepository.save(course);
    courseId = course.getId();
  }

  private LessonRequestDto validLessonRequest() {
    return new LessonRequestDto(
        "Lesson 1",
        60,
        courseId
    );
  }

  @Test
  void createLesson_shouldReturnCreatedAndLessonResponse() throws Exception {
    var request = validLessonRequest();

    mockMvc.perform(post("/api/v1/lessons")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.title").value("Lesson 1"))
        .andExpect(jsonPath("$.duration").value(60))
        .andExpect(jsonPath("$.courseId").value(courseId.toString()));
  }

  @Test
  void createLesson_shouldReturnBadRequestOnInvalidData() throws Exception {
    var invalidRequest = new LessonRequestDto(
        "",
        0,
        null
    );

    mockMvc.perform(post("/api/v1/lessons")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", containsString("Validation failed")))
        .andExpect(jsonPath("$.errors.title").exists())
        .andExpect(jsonPath("$.errors.duration").exists())
        .andExpect(jsonPath("$.errors.courseId").exists());
  }

  @Test
  void getAllLessons_shouldReturnEmptyListInitially() throws Exception {
    mockMvc.perform(get("/api/v1/lessons")
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  void getAllLessons_shouldReturnListOfLessons() throws Exception {
    var request = validLessonRequest();
    mockMvc.perform(post("/api/v1/lessons")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/v1/lessons")
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].title").value("Lesson 1"));
  }

  @Test
  void updateLesson_shouldUpdateAndReturnLesson() throws Exception {
    var request = validLessonRequest();
    String response = mockMvc.perform(post("/api/v1/lessons")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID lessonId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    var updateRequest = new LessonRequestDto(
        "Updated Lesson",
        90,
        courseId
    );

    mockMvc.perform(put("/api/v1/lessons/{id}", lessonId)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Lesson"))
        .andExpect(jsonPath("$.duration").value(90))
        .andExpect(jsonPath("$.courseId").value(courseId.toString()));
  }

  @Test
  void updateLesson_shouldReturnNotFoundForInvalidId() throws Exception {
    UUID randomId = UUID.randomUUID();
    var updateRequest = validLessonRequest();

    mockMvc.perform(put("/api/v1/lessons/{id}", randomId)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Lesson not found")));
  }

  @Test
  void updateLesson_shouldReturnBadRequestOnInvalidData() throws Exception {
    var request = validLessonRequest();
    String response = mockMvc.perform(post("/api/v1/lessons")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID lessonId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    var invalidUpdate = new LessonRequestDto(
        "",
        0,
        null
    );

    mockMvc.perform(put("/api/v1/lessons/{id}", lessonId)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUpdate)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", containsString("Validation failed")))
        .andExpect(jsonPath("$.errors.title").exists())
        .andExpect(jsonPath("$.errors.duration").exists())
        .andExpect(jsonPath("$.errors.courseId").exists());
  }

  @Test
  void deleteLesson_shouldDeleteAndReturnNoContent() throws Exception {
    var request = validLessonRequest();
    String response = mockMvc.perform(post("/api/v1/lessons")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID lessonId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    mockMvc.perform(delete("/api/v1/lessons/{id}", lessonId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/lessons")
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  void deleteLesson_shouldReturnNotFoundForInvalidId() throws Exception {
    UUID randomId = UUID.randomUUID();
    mockMvc.perform(delete("/api/v1/lessons/{id}", randomId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Lesson not found")));
  }
}
