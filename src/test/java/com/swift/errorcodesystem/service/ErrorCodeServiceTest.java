package com.swift.errorcodesystem.service;


import com.swift.errorcodesystem.entity.Category;
import com.swift.errorcodesystem.entity.ErrorCode;
import com.swift.errorcodesystem.entity.Project;
import com.swift.errorcodesystem.entity.Module;
import com.swift.errorcodesystem.repository.CategoryRepository;
import com.swift.errorcodesystem.repository.ErrorCodeRepository;
import com.swift.errorcodesystem.repository.ModuleRepository;
import com.swift.errorcodesystem.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorCodeServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private ErrorCodeRepository errorCodeRepository;

    @InjectMocks
    private ErrorCodeService errorCodeService;

    private Project testProject;
    private Category testCategory;
    private Module testModule;
    private ErrorCode testErrorCode;

    @BeforeEach
    void setUp() {
        testProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .code("01")
                .owner("Test Team")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        testCategory = Category.builder()
                .id(1L)
                .name("Test Category")
                .code("01")
                .project(testProject)
                .build();

        testModule = Module.builder()
                .id(1L)
                .name("Test Module")
                .code("01")
                .category(testCategory)
                .purpose("Test Purpose")
                .build();

        testErrorCode = ErrorCode.builder()
                .id(1L)
                .code("01-01-01-0001")
                .message("Test error message")
                .severity(ErrorCode.ErrorSeverity.MEDIUM)
                .httpStatus(ErrorCode.HttpStatus.BAD_REQUEST)
                .isRetryable(false)
                .module(testModule)
                .build();
    }

    @Test
    void createProject_Success() {
        when(projectRepository.existsByName(any())).thenReturn(false);
        when(projectRepository.existsByCode(any())).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = errorCodeService.createProject(testProject);

        assertNotNull(result);
        assertEquals(testProject.getName(), result.getName());
        assertEquals(testProject.getCode(), result.getCode());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void createProject_WithDuplicateName_ThrowsException() {
        when(projectRepository.existsByName(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            errorCodeService.createProject(testProject);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createProject_WithInvalidCode_ThrowsException() {
        testProject.setCode("1"); // Invalid code - should be 2 digits

        assertThrows(IllegalArgumentException.class, () -> {
            errorCodeService.createProject(testProject);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createCategory_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(categoryRepository.existsByProjectIdAndCode(any(), any())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = errorCodeService.createCategory(1L, testCategory);

        assertNotNull(result);
        assertEquals(testCategory.getName(), result.getName());
        assertEquals(testCategory.getCode(), result.getCode());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createModule_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(moduleRepository.existsByCategoryIdAndCode(any(), any())).thenReturn(false);
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);

        Module result = errorCodeService.createModule(1L, testModule);

        assertNotNull(result);
        assertEquals(testModule.getName(), result.getName());
        assertEquals(testModule.getCode(), result.getCode());
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void createErrorCode_Success() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(errorCodeRepository.findMaxSequenceByModuleId(1L)).thenReturn(Optional.of(0));
        when(errorCodeRepository.existsByCode(any())).thenReturn(false);
        when(errorCodeRepository.save(any(ErrorCode.class))).thenReturn(testErrorCode);

        ErrorCode result = errorCodeService.createErrorCode(1L, testErrorCode);

        assertNotNull(result);
        assertEquals(testErrorCode.getMessage(), result.getMessage());
        verify(errorCodeRepository, times(1)).save(any(ErrorCode.class));
    }

    @Test
    void createErrorCode_GeneratesCorrectCodeFormat() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(errorCodeRepository.findMaxSequenceByModuleId(1L)).thenReturn(Optional.of(5));
        when(errorCodeRepository.existsByCode(any())).thenReturn(false);
        when(errorCodeRepository.save(any(ErrorCode.class))).thenReturn(testErrorCode);

        ErrorCode result = errorCodeService.createErrorCode(1L, testErrorCode);

        assertNotNull(result);
        // Should generate code in format: 01-01-01-0006 (next sequence after 5)
        verify(errorCodeRepository).save(argThat(errorCode ->
                errorCode.getCode().equals("01-01-01-0006")));
    }

    @Test
    void getProjectById_Found() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        Optional<Project> result = errorCodeService.getProjectById(1L);

        assertTrue(result.isPresent());
        assertEquals(testProject.getId(), result.get().getId());
    }

    @Test
    void getProjectById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Project> result = errorCodeService.getProjectById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void getErrorCodesByModuleId_Success() {
        when(errorCodeRepository.findByModuleId(1L)).thenReturn(List.of(testErrorCode));

        List<ErrorCode> result = errorCodeService.getErrorCodesByModuleId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testErrorCode.getCode(), result.get(0).getCode());
    }

    @Test
    void updateErrorCode_Success() {
        when(errorCodeRepository.findById(1L)).thenReturn(Optional.of(testErrorCode));
        when(errorCodeRepository.save(any(ErrorCode.class))).thenReturn(testErrorCode);

        ErrorCode updatedErrorCode = ErrorCode.builder()
                .message("Updated message")
                .description("Updated description")
                .suggestedAction("Updated action")
                .severity(ErrorCode.ErrorSeverity.HIGH)
                .httpStatus(ErrorCode.HttpStatus.INTERNAL_SERVER_ERROR)
                .isRetryable(true)
                .build();

        ErrorCode result = errorCodeService.updateErrorCode(1L, updatedErrorCode);

        assertNotNull(result);
        assertEquals("Updated message", result.getMessage());
        assertEquals(ErrorCode.ErrorSeverity.HIGH, result.getSeverity());
        verify(errorCodeRepository, times(1)).save(any(ErrorCode.class));
    }

    @Test
    void searchErrorCodes_WithTerm() {
        when(errorCodeRepository.findAll()).thenReturn(List.of(testErrorCode));

        List<ErrorCode> result = errorCodeService.searchErrorCodes("test");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void searchErrorCodes_NoMatches() {
        when(errorCodeRepository.findAll()).thenReturn(List.of(testErrorCode));

        List<ErrorCode> result = errorCodeService.searchErrorCodes("nonexistent");

        assertTrue(result.isEmpty());
    }
}
