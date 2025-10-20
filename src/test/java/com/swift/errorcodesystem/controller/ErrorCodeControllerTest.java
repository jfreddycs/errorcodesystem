package com.swift.errorcodesystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swift.errorcodesystem.dto.ErrorCodeDto;
import com.swift.errorcodesystem.entity.ErrorCode;
import com.swift.errorcodesystem.entity.Project;
import com.swift.errorcodesystem.service.ErrorCodeService;
import com.swift.errorcodesystem.entity.Module;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ErrorCodeController.class)
class ErrorCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ErrorCodeService errorCodeService;

    @Test
    void createProject_Success() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .code("01")
                .owner("Test Team")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        when(errorCodeService.createProject(any(Project.class))).thenReturn(project);

        ErrorCodeDto.CreateProjectRequest request = ErrorCodeDto.CreateProjectRequest.builder()
                .name("Test Project")
                .code("01")
                .owner("Test Team")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/error-codes/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.code").value("01"));
    }

    @Test
    void createProject_WithInvalidCode_ReturnsBadRequest() throws Exception {
        ErrorCodeDto.CreateProjectRequest request = ErrorCodeDto.CreateProjectRequest.builder()
                .name("Test Project")
                .code("1") // Invalid - should be 2 digits
                .owner("Test Team")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/error-codes/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllProjects_Success() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .code("01")
                .owner("Test Team")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        when(errorCodeService.getAllProjects()).thenReturn(List.of(project));

        mockMvc.perform(get("/api/v1/error-codes/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Project"))
                .andExpect(jsonPath("$[0].code").value("01"));
    }

    @Test
    void getProject_NotFound() throws Exception {
        when(errorCodeService.getProjectByIdWithCategories(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/error-codes/projects/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createErrorCode_Success() throws Exception {
        Module module = Module.builder()
                .id(1L)
                .name("Test Module")
                .build();

        ErrorCode errorCode = ErrorCode.builder()
                .id(1L)
                .code("01-01-01-0001")
                .message("Test error message")
                .severity(ErrorCode.ErrorSeverity.MEDIUM)
                .httpStatus(ErrorCode.HttpStatus.BAD_REQUEST)
                .isRetryable(false)
                .module(module)
                .build();

        when(errorCodeService.createErrorCode(anyLong(), any(ErrorCode.class))).thenReturn(errorCode);

        ErrorCodeDto.CreateErrorCodeRequest request = ErrorCodeDto.CreateErrorCodeRequest.builder()
                .message("Test error message")
                .severity(ErrorCode.ErrorSeverity.MEDIUM)
                .httpStatus(ErrorCode.HttpStatus.BAD_REQUEST)
                .isRetryable(false)
                .build();

        mockMvc.perform(post("/api/v1/error-codes/modules/1/error-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("01-01-01-0001"))
                .andExpect(jsonPath("$.severity").value("MEDIUM"));
    }

    @Test
    void getErrorCodeByCode_Success() throws Exception {
        Module module = Module.builder()
                .id(1L)
                .name("Test Module")
                .build();

        ErrorCode errorCode = ErrorCode.builder()
                .id(1L)
                .code("01-01-01-0001")
                .message("Test error message")
                .severity(ErrorCode.ErrorSeverity.MEDIUM)
                .httpStatus(ErrorCode.HttpStatus.BAD_REQUEST)
                .isRetryable(false)
                .module(module)
                .build();

        when(errorCodeService.getErrorCodeByCode("01-01-01-0001")).thenReturn(Optional.of(errorCode));

        mockMvc.perform(get("/api/v1/error-codes/code/01-01-01-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("01-01-01-0001"))
                .andExpect(jsonPath("$.message").value("Test error message"));
    }

    @Test
    void searchErrorCodes_Success() throws Exception {
        Module module = Module.builder()
                .id(1L)
                .name("Test Module")
                .build();

        ErrorCode errorCode = ErrorCode.builder()
                .id(1L)
                .code("01-01-01-0001")
                .message("Test error message")
                .severity(ErrorCode.ErrorSeverity.MEDIUM)
                .httpStatus(ErrorCode.HttpStatus.BAD_REQUEST)
                .isRetryable(false)
                .module(module)
                .build();

        when(errorCodeService.getAllErrorCodes()).thenReturn(List.of(errorCode));

        ErrorCodeDto.SearchRequest request = ErrorCodeDto.SearchRequest.builder()
                .severity(ErrorCode.ErrorSeverity.MEDIUM)
                .build();

        mockMvc.perform(post("/api/v1/error-codes/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("01-01-01-0001"));
    }
}