package com.example.lms.integration.student;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.lms.course.model.Course;
import com.example.lms.course.model.CourseSettings;
import com.example.lms.course.repository.CourseRepository;
import com.example.lms.enums.LocaleCode;
import com.example.lms.student.dto.StudentRequestDto;
import com.example.lms.student.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class StudentControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StudentRepository studentRepository;

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
    studentRepository.deleteAll();
    courseRepository.deleteAll();
    Course course = new Course();
    course.setTitle("Test Course");
    course.setDescription("desc");
    course.setPrice(BigDecimal.TEN);
    course.setCoinsPaid(BigDecimal.ONE);
    CourseSettings settings = new CourseSettings();
    settings.setStartDate(LocalDateTime.now().minusDays(1));
    settings.setEndDate(LocalDateTime.now().plusDays(10));
    settings.setIsPublic(true);
    course.setSettings(settings);
    course = courseRepository.save(course);
    courseId = course.getId();
  }

  private StudentRequestDto validStudentRequest() {
    return new StudentRequestDto(
        "John",
        "Doe",
        "john.doe@example.com",
        LocalDate.of(2000, 1, 1),
        BigDecimal.valueOf(100),
        LocaleCode.EN
    );
  }

  @Test
  void createStudent_shouldReturnCreatedAndStudentResponse() throws Exception {
    var request = validStudentRequest();

    mockMvc.perform(post("/api/v1/students")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.coins").value(100));
  }

  @Test
  void createStudent_shouldReturnBadRequestOnInvalidData() throws Exception {
    var invalidRequest = new StudentRequestDto(
        "",
        "",
        "invalid-email",
        LocalDate.now().plusDays(1),
        BigDecimal.valueOf(-10),
        LocaleCode.EN
    );

    mockMvc.perform(post("/api/v1/students")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", containsString("Validation failed")))
        .andExpect(jsonPath("$.errors.firstName").exists())
        .andExpect(jsonPath("$.errors.lastName").exists())
        .andExpect(jsonPath("$.errors.email").exists())
        .andExpect(jsonPath("$.errors.dateOfBirth").exists())
        .andExpect(jsonPath("$.errors.coins").exists());
  }

  @Test
  void getAllStudents_shouldReturnEmptyListInitially() throws Exception {
    mockMvc.perform(get("/api/v1/students")
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void getAllStudents_shouldReturnListOfStudents() throws Exception {
    var request = validStudentRequest();
    mockMvc.perform(post("/api/v1/students")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/v1/students")
        .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].firstName").value("John"));
  }

  @Test
  void updateStudent_shouldUpdateAndReturnStudent() throws Exception {
    var request = validStudentRequest();
    String response = mockMvc.perform(post("/api/v1/students")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID studentId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    var updateRequest = new StudentRequestDto(
        "Jane",
        "Smith",
        "jane.smith@example.com",
        LocalDate.of(1995, 5, 5),
        BigDecimal.valueOf(50),
        LocaleCode.EN
    );

    mockMvc.perform(put("/api/v1/students/{id}", studentId)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Smith"))
        .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
        .andExpect(jsonPath("$.coins").value(50));
  }

  @Test
  void updateStudent_shouldReturnNotFoundForInvalidId() throws Exception {
    UUID randomId = UUID.randomUUID();
    var updateRequest = validStudentRequest();

    mockMvc.perform(put("/api/v1/students/{id}", randomId)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Student not found")));
  }

  @Test
  void updateStudent_shouldReturnBadRequestOnInvalidData() throws Exception {
    var request = validStudentRequest();
    String response = mockMvc.perform(post("/api/v1/students")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID studentId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    var invalidUpdate = new StudentRequestDto(
        "",
        "",
        "bad-email",
        LocalDate.now().plusDays(1),
        BigDecimal.valueOf(-1),
        LocaleCode.EN
    );

    mockMvc.perform(put("/api/v1/students/{id}", studentId)
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUpdate)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", containsString("Validation failed")))
        .andExpect(jsonPath("$.errors.firstName").exists())
        .andExpect(jsonPath("$.errors.lastName").exists())
        .andExpect(jsonPath("$.errors.email").exists())
        .andExpect(jsonPath("$.errors.dateOfBirth").exists())
        .andExpect(jsonPath("$.errors.coins").exists());
  }

  @Test
  void deleteStudent_shouldDeleteAndReturnNoContent() throws Exception {
    var request = validStudentRequest();
    String response = mockMvc.perform(post("/api/v1/students")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID studentId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    mockMvc.perform(delete("/api/v1/students/{id}", studentId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/students")
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void deleteStudent_shouldReturnNotFoundForInvalidId() throws Exception {
    UUID randomId = UUID.randomUUID();
    mockMvc.perform(delete("/api/v1/students/{id}", randomId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Student not found")));
  }

  @Test
  void buyCourseWithCoins_shouldSucceed() throws Exception {
    var request = validStudentRequest();
    String response = mockMvc.perform(post("/api/v1/students")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID studentId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    mockMvc.perform(post("/api/v1/students/{studentId}/buy-course/{courseId}", studentId, courseId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNoContent());
  }

  @Test
  void buyCourseWithCoins_shouldReturnNotFoundForInvalidStudent() throws Exception {
    UUID randomStudentId = UUID.randomUUID();
    mockMvc.perform(post("/api/v1/students/{studentId}/buy-course/{courseId}", randomStudentId, courseId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Student not found")));
  }

  @Test
  void buyCourseWithCoins_shouldReturnNotFoundForInvalidCourse() throws Exception {
    var request = validStudentRequest();
    String response = mockMvc.perform(post("/api/v1/students")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn().getResponse().getContentAsString();
    UUID studentId = UUID.fromString(objectMapper.readTree(response).get("id").asText());
    UUID randomCourseId = UUID.randomUUID();

    mockMvc.perform(post("/api/v1/students/{studentId}/buy-course/{courseId}", studentId, randomCourseId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Course not found")));
  }
}
