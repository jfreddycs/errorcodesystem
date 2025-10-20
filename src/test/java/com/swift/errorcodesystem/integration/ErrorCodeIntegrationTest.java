package com.swift.errorcodesystem.integration;

import com.swift.errorcodesystem.dto.ErrorCodeDto;
import com.swift.errorcodesystem.entity.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ErrorCodeIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Basic context loading test
    }

    @Test
    void createProjectAndRetrieve_Success() {
        // Create project
        ErrorCodeDto.CreateProjectRequest request = ErrorCodeDto.CreateProjectRequest.builder()
                .name("Integration Test Project")
                .code("99")
                .owner("Integration Team")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        ResponseEntity<ErrorCodeDto.ProjectResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/error-codes/projects",
                request,
                ErrorCodeDto.ProjectResponse.class
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals("Integration Test Project", createResponse.getBody().getName());

        // Retrieve project
        ResponseEntity<ErrorCodeDto.ProjectResponse> getResponse = restTemplate.getForEntity(
                "/api/v1/error-codes/projects/" + createResponse.getBody().getId(),
                ErrorCodeDto.ProjectResponse.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("Integration Test Project", getResponse.getBody().getName());
    }

    @Test
    void getAllProjects_ReturnsOk() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/error-codes/projects", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void searchErrorCodes_ReturnsResults() {
        ErrorCodeDto.SearchRequest request = ErrorCodeDto.SearchRequest.builder()
                .searchTerm("authentication")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/error-codes/search",
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
