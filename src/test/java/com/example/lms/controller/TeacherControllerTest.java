package com.example.lms.controller;

import com.example.lms.dao.TeacherRepository;
import com.example.lms.dto.TeacherRequestDto;
import com.example.lms.dto.TeacherResponseDto;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.models.Teacher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeacherControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
            .withUsername("postgres")
            .withPassword("postgres")
            .withDatabaseName("test-lms");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");

    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TeacherRepository teacherRepository;

    Teacher teacher = new Teacher();

    @BeforeEach
    void setUp() {
        teacher.setFirstName("Иван");
        teacher.setLastName("Иванов");
        teacherRepository.save(teacher);
    }

    @AfterEach
    void cleanUp() {
        teacherRepository.deleteAll();
    }

    @Test
    void createTeacher() {

        String firstName = "Петр";
        String lastName = "Петров";
        TeacherRequestDto requestDto = new TeacherRequestDto(
                firstName,
                lastName
        );

        ResponseEntity<TeacherResponseDto> response = restTemplate.postForEntity(
                "/api/v1/teachers",
                requestDto,
                TeacherResponseDto.class
        );

        assertThat(response.getStatusCode())
                .as("Статус ответа должен быть 201 CREATED")
                .isEqualTo(HttpStatus.CREATED);

        assertThat(response.getBody())
                .as("Тело ответа не должно быть null")
                .isNotNull();

        TeacherResponseDto createdTeacher = response.getBody();

        assertNotNull(createdTeacher);
        Teacher savedTeacher = teacherInDb(createdTeacher.id());

        assertTeacherResponseDto(createdTeacher, firstName, lastName);
        assertTeacherInDb(savedTeacher,  firstName, lastName);

        //TODO можно дописать проверки по длине строки + не нулевому значению в негативном контексте
        //TODO создать второго учителя и проверить что БД две записи и проверки на качество полей
    }

    private void assertTeacherResponseDto(
            TeacherResponseDto teacherResponseDto,
            String expectedFirstName,
            String expectedLastName
    ) {

        assertNotNull(teacherResponseDto);
        assertThat(teacherResponseDto.id())
                .as("поле ID должно быть сгенерировано ")
                .isNotNull();
        assertThat(teacherResponseDto.firstName())
                .as("Имя должно соответствовать запросы")
                .isEqualTo(expectedFirstName);
        assertThat(teacherResponseDto.lastName())
                .as("Фамилия должна соответствовать запросы")
                .isEqualTo(expectedLastName);

    }

    private void assertTeacherInDb(
            Teacher savedTeacher,
            String expectedFirstName,
            String expectedLastName
    ) {

        assertThat(savedTeacher.getFirstName())
                .as("Имя в БД должно соответствовать")
                .isEqualTo(expectedFirstName);
        assertThat(savedTeacher.getLastName())
                .as("Фамилия в БД должна соответстовать")
                .isEqualTo(expectedLastName);
    }

    @Test
    void getAllTeachers() throws IOException {

        int newTeacherCount = 5;
        createMockTeachers(newTeacherCount);

        String url = UriComponentsBuilder.fromUriString("/api/v1/teachers").toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        String testResponse = restTemplate.getForEntity(url, String.class).getBody();

        Map<String, Integer> page = parsingResponseToPageMap(testResponse);
        List<Teacher> parsContent = parsingResponseToContentMap(testResponse);

        if (newTeacherCount < 10) {
            boolean isTeacherInResponse = teacherInResponse(parsContent, teacher);
            System.out.println("isTeacherInResponse " + isTeacherInResponse);
            assertThat(isTeacherInResponse).isTrue();
        };

        int countAfterRequest = page.get("totalElements");
        int teacherCountAfterRequest = newTeacherCount + 1;

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(teacherCountAfterRequest).isEqualTo(countAfterRequest);

        //TODO решить проверяю-ли пагинацию (проверка на отрицательную страницу и т.д)

    }

    boolean teacherInResponse(List<Teacher> parsedResponse, Teacher findedTeacher) {
        boolean exists = parsedResponse.stream()
                .anyMatch(
                        teacher -> teacher.getExternalId()
                                .equals(findedTeacher.getExternalId())
                );
        return exists;
    }

    Map<String, Integer> parsingResponseToPageMap(String response) throws JsonProcessingException {

        assertNotNull(response);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        assertThat(root).isNotNull();

        JsonNode pages = root.get("page");
        assertThat(pages).isNotNull();

        Map<String, Integer> parsedPage = new HashMap<>();
        parsedPage.put("size", pages.get("size").asInt());
        parsedPage.put("totalElements", pages.get("totalElements").asInt());
        parsedPage.put("totalPages", pages.get("totalPages").asInt());

        return parsedPage;
    }

    List<Teacher> parsingResponseToContentMap(String response) throws IOException {
        assertNotNull(response);
        List<Teacher> parseContent = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        assertThat(root).isNotNull();

        JsonNode content = root.get("content");

        assertThat(content.isArray()).isTrue();
        assertThat(content.size()).isGreaterThan(0);
        assertThat(content).isNotNull();

        for (JsonNode jsonNode : content) {
            UUID externalId = UUID.fromString(jsonNode.get("id").asText());
            String firstName = jsonNode.get("firstName").asText();
            String lastName = jsonNode.get("lastName").asText();

            Teacher newTeacher = new Teacher();
            newTeacher.setExternalId(externalId);
            newTeacher.setFirstName(firstName);
            newTeacher.setLastName(lastName);

            parseContent.add(newTeacher);
        }
        return parseContent;
    }

    void createMockTeachers(int newTeacherCount) {
        for (int i = 0; i < newTeacherCount; i++) {
            teacher = new Teacher();
            teacher.setLastName("Новиков" + i);
            teacher.setFirstName("Владислав" + i);
            teacherRepository.save(teacher);
        }
    }

    @Test
    void getTeacherByExternalId() {
        ResponseEntity<TeacherResponseDto> response = restTemplate.getForEntity(
                "/api/v1/teachers/{externalId}",
                TeacherResponseDto.class,
                teacher.getExternalId()
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertNotNull(response.getBody());
        UUID foundTeacherId = response.getBody().id();
        assertIsEqualExternalId(
                teacher.getExternalId(),
                foundTeacherId);
    }

    @Test
    void deleteTeacher() {
        UUID externalId = teacher.getExternalId();

        restTemplate.delete(
                "/api/v1/teachers/{externalId}",
                externalId
        );

        ResponseEntity<TeacherResponseDto> responseAfterDelete = restTemplate.getForEntity(
                "/api/v1/teachers/{externalId}",
                TeacherResponseDto.class,
                externalId
        );

        assertThat(responseAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void restoreTeacher() {
        UUID externalId = teacher.getExternalId();

        restTemplate.delete(
                "/api/v1/teachers/{externalId}",
                externalId
        );

        ResponseEntity<TeacherResponseDto> response = restTemplate.exchange(
                "/api/v1/teachers/recovery-teacher/{externalId}",
                HttpMethod.PATCH,
                null,  // ← Нет тела запроса
                TeacherResponseDto.class,
                externalId
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertNotNull(response.getBody());
        UUID teacherAfterRestoreExternalId = response.getBody().id();

        assertIsEqualExternalId(
                externalId,
                teacherAfterRestoreExternalId);
    }

    void assertIsEqualExternalId(UUID externalId, UUID expectedExternalId) {
        assertThat(externalId).isEqualTo(expectedExternalId);
    }

    @Test
    void updateTeacher() {
        UUID externalId = teacher.getExternalId();

        String newFirstName = "Василий";
        String newLastName = "Васильев";

        TeacherRequestDto requestDto = new TeacherRequestDto(newFirstName, newLastName);

        ResponseEntity<TeacherResponseDto> response = restTemplate.exchange(
                "/api/v1/teachers/update-teacher/{externalId}",
                HttpMethod.PUT,
                new HttpEntity<>(requestDto),
                TeacherResponseDto.class,
                externalId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        TeacherResponseDto updatedTeacher = response.getBody();
        //Проверяю в ответе
        assertThat(updatedTeacher.firstName().equals(newFirstName));
        assertThat(updatedTeacher.lastName().equals(newLastName));

        assertNotNull(updatedTeacher);

        Teacher teacherInDb = teacherInDb(updatedTeacher.id());
        //Проверяю в БД
        assertThat(teacherInDb.getFirstName()).isEqualTo(newFirstName);
        assertThat(teacherInDb.getLastName()).isEqualTo(newLastName);

    }

    Teacher teacherInDb(UUID externalId) {
        return teacherRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподавателя с ID " + externalId + " не найдено")
                );
    }
}