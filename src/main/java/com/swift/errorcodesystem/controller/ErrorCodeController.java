package com.swift.errorcodesystem.controller;

import com.swift.errorcodesystem.dto.ErrorCodeDto;
import com.swift.errorcodesystem.entity.Category;
import com.swift.errorcodesystem.entity.ErrorCode;
import com.swift.errorcodesystem.entity.Module;
import com.swift.errorcodesystem.entity.Project;
import com.swift.errorcodesystem.service.ErrorCodeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/error-codes")
@RequiredArgsConstructor
@Slf4j
public class ErrorCodeController {

    private final ErrorCodeService errorCodeService;

    // Project endpoints
    @PostMapping("/projects")
    public ResponseEntity<ErrorCodeDto.ProjectResponse> createProject(
            @Valid @RequestBody ErrorCodeDto.CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .code(request.getCode())
                .owner(request.getOwner())
                .status(request.getStatus())
                .build();

        Project savedProject = errorCodeService.createProject(project);
        return ResponseEntity.ok(ErrorCodeDto.ProjectResponse.fromEntityWithoutCategories(savedProject));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ErrorCodeDto.ProjectResponse>> getAllProjects(
            @RequestParam(defaultValue = "false") boolean withCategories) {
        List<ErrorCodeDto.ProjectResponse> projects;

        if (withCategories) {
            projects = errorCodeService.getAllProjects().stream()
                    .map(ErrorCodeDto.ProjectResponse::fromEntity)
                    .collect(Collectors.toList());
        } else {
            projects = errorCodeService.getAllProjects().stream()
                    .map(ErrorCodeDto.ProjectResponse::fromEntityWithoutCategories)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ErrorCodeDto.ProjectResponse> getProject(@PathVariable Long id) {
        return errorCodeService.getProjectByIdWithCategories(id)
                .map(project -> ResponseEntity.ok(ErrorCodeDto.ProjectResponse.fromEntity(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ErrorCodeDto.ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ErrorCodeDto.CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .code(request.getCode())
                .owner(request.getOwner())
                .status(request.getStatus())
                .build();

        Project updatedProject = errorCodeService.updateProject(id, project);
        return ResponseEntity.ok(ErrorCodeDto.ProjectResponse.fromEntityWithoutCategories(updatedProject));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        errorCodeService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // Category endpoints
    @PostMapping("/projects/{projectId}/categories")
    public ResponseEntity<ErrorCodeDto.CategoryResponse> createCategory(
            @PathVariable Long projectId,
            @Valid @RequestBody ErrorCodeDto.CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .code(request.getCode())
                .build();

        Category savedCategory = errorCodeService.createCategory(projectId, category);
        return ResponseEntity.ok(ErrorCodeDto.CategoryResponse.fromEntityWithoutModules(savedCategory));
    }

    @GetMapping("/projects/{projectId}/categories")
    public ResponseEntity<List<ErrorCodeDto.CategoryResponse>> getProjectCategories(@PathVariable Long projectId) {
        List<ErrorCodeDto.CategoryResponse> categories = errorCodeService.getCategoriesByProjectId(projectId).stream()
                .map(ErrorCodeDto.CategoryResponse::fromEntityWithoutModules)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ErrorCodeDto.CategoryResponse> getCategory(@PathVariable Long id) {
        return errorCodeService.getCategoryByIdWithModules(id)
                .map(category -> ResponseEntity.ok(ErrorCodeDto.CategoryResponse.fromEntity(category)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Module endpoints
    @PostMapping("/categories/{categoryId}/modules")
    public ResponseEntity<ErrorCodeDto.ModuleResponse> createModule(
            @PathVariable Long categoryId,
            @Valid @RequestBody ErrorCodeDto.CreateModuleRequest request) {
        Module module = Module.builder()
                .name(request.getName())
                .description(request.getDescription())
                .code(request.getCode())
                .purpose(request.getPurpose())
                .build();

        Module savedModule = errorCodeService.createModule(categoryId, module);
        return ResponseEntity.ok(ErrorCodeDto.ModuleResponse.fromEntityWithoutErrorCodes(savedModule));
    }

    @GetMapping("/categories/{categoryId}/modules")
    public ResponseEntity<List<ErrorCodeDto.ModuleResponse>> getCategoryModules(@PathVariable Long categoryId) {
        List<ErrorCodeDto.ModuleResponse> modules = errorCodeService.getModulesByCategoryId(categoryId).stream()
                .map(ErrorCodeDto.ModuleResponse::fromEntityWithoutErrorCodes)
                .collect(Collectors.toList());
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/modules/{id}")
    public ResponseEntity<ErrorCodeDto.ModuleResponse> getModule(@PathVariable Long id) {
        return errorCodeService.getModuleByIdWithErrorCodes(id)
                .map(module -> ResponseEntity.ok(ErrorCodeDto.ModuleResponse.fromEntity(module)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Error Code endpoints
    @PostMapping("/modules/{moduleId}/error-codes")
    public ResponseEntity<ErrorCodeDto.ErrorCodeResponse> createErrorCode(
            @PathVariable Long moduleId,
            @Valid @RequestBody ErrorCodeDto.CreateErrorCodeRequest request) {
        ErrorCode errorCode = ErrorCode.builder()
                .message(request.getMessage())
                .description(request.getDescription())
                .suggestedAction(request.getSuggestedAction())
                .severity(request.getSeverity())
                .httpStatus(request.getHttpStatus())
                .isRetryable(request.getIsRetryable())
                .build();

        ErrorCode savedErrorCode = errorCodeService.createErrorCode(moduleId, errorCode);
        return ResponseEntity.ok(ErrorCodeDto.ErrorCodeResponse.fromEntity(savedErrorCode));
    }

    @GetMapping("/modules/{moduleId}/error-codes")
    public ResponseEntity<List<ErrorCodeDto.ErrorCodeResponse>> getModuleErrorCodes(@PathVariable Long moduleId) {
        List<ErrorCodeDto.ErrorCodeResponse> errorCodes = errorCodeService.getErrorCodesByModuleId(moduleId).stream()
                .map(ErrorCodeDto.ErrorCodeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(errorCodes);
    }

    @GetMapping("/projects/{projectId}/error-codes")
    public ResponseEntity<List<ErrorCodeDto.ErrorCodeResponse>> getProjectErrorCodes(@PathVariable Long projectId) {
        List<ErrorCodeDto.ErrorCodeResponse> errorCodes = errorCodeService.getErrorCodesByProjectId(projectId).stream()
                .map(ErrorCodeDto.ErrorCodeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(errorCodes);
    }

    @GetMapping("/categories/{categoryId}/error-codes")
    public ResponseEntity<List<ErrorCodeDto.ErrorCodeResponse>> getCategoryErrorCodes(@PathVariable Long categoryId) {
        List<ErrorCodeDto.ErrorCodeResponse> errorCodes = errorCodeService.getErrorCodesByCategoryId(categoryId).stream()
                .map(ErrorCodeDto.ErrorCodeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(errorCodes);
    }

    @GetMapping("/error-codes")
    public ResponseEntity<List<ErrorCodeDto.ErrorCodeResponse>> getAllErrorCodes() {
        List<ErrorCodeDto.ErrorCodeResponse> errorCodes = errorCodeService.getAllErrorCodes().stream()
                .map(ErrorCodeDto.ErrorCodeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(errorCodes);
    }

    @GetMapping("/error-codes/{id}")
    public ResponseEntity<ErrorCodeDto.ErrorCodeResponse> getErrorCode(@PathVariable Long id) {
        return errorCodeService.getErrorCodeById(id)
                .map(errorCode -> ResponseEntity.ok(ErrorCodeDto.ErrorCodeResponse.fromEntity(errorCode)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/error-codes/code/{code}")
    public ResponseEntity<ErrorCodeDto.ErrorCodeResponse> getErrorCodeByCode(@PathVariable String code) {
        return errorCodeService.getErrorCodeByCode(code)
                .map(errorCode -> ResponseEntity.ok(ErrorCodeDto.ErrorCodeResponse.fromEntity(errorCode)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/error-codes/{id}")
    public ResponseEntity<ErrorCodeDto.ErrorCodeResponse> updateErrorCode(
            @PathVariable Long id,
            @Valid @RequestBody ErrorCodeDto.CreateErrorCodeRequest request) {
        ErrorCode errorCode = ErrorCode.builder()
                .message(request.getMessage())
                .description(request.getDescription())
                .suggestedAction(request.getSuggestedAction())
                .severity(request.getSeverity())
                .httpStatus(request.getHttpStatus())
                .isRetryable(request.getIsRetryable())
                .build();

        ErrorCode updatedErrorCode = errorCodeService.updateErrorCode(id, errorCode);
        return ResponseEntity.ok(ErrorCodeDto.ErrorCodeResponse.fromEntity(updatedErrorCode));
    }

    @DeleteMapping("/error-codes/{id}")
    public ResponseEntity<Void> deleteErrorCode(@PathVariable Long id) {
        errorCodeService.deleteErrorCode(id);
        return ResponseEntity.noContent().build();
    }

    // Search endpoints
    @PostMapping("/search")
    public ResponseEntity<List<ErrorCodeDto.ErrorCodeResponse>> searchErrorCodes(
            @RequestBody ErrorCodeDto.SearchRequest searchRequest) {

        List<ErrorCode> errorCodes;

        if (searchRequest.getSearchTerm() != null && !searchRequest.getSearchTerm().isEmpty()) {
            errorCodes = errorCodeService.searchErrorCodes(searchRequest.getSearchTerm());
        } else {
            errorCodes = errorCodeService.getAllErrorCodes();
        }

        // Apply filters
        if (searchRequest.getSeverity() != null) {
            errorCodes = errorCodes.stream()
                    .filter(ec -> ec.getSeverity() == searchRequest.getSeverity())
                    .collect(Collectors.toList());
        }

        if (searchRequest.getHttpStatus() != null) {
            errorCodes = errorCodes.stream()
                    .filter(ec -> ec.getHttpStatus() == searchRequest.getHttpStatus())
                    .collect(Collectors.toList());
        }

        if (searchRequest.getProjectCode() != null) {
            errorCodes = errorCodes.stream()
                    .filter(ec -> ec.getModule().getCategory().getProject().getCode().equals(searchRequest.getProjectCode()))
                    .collect(Collectors.toList());
        }

        if (searchRequest.getCategoryCode() != null) {
            errorCodes = errorCodes.stream()
                    .filter(ec -> ec.getModule().getCategory().getCode().equals(searchRequest.getCategoryCode()))
                    .collect(Collectors.toList());
        }

        if (searchRequest.getModuleCode() != null) {
            errorCodes = errorCodes.stream()
                    .filter(ec -> ec.getModule().getCode().equals(searchRequest.getModuleCode()))
                    .collect(Collectors.toList());
        }

        if (searchRequest.getIsRetryable() != null) {
            errorCodes = errorCodes.stream()
                    .filter(ec -> ec.getIsRetryable().equals(searchRequest.getIsRetryable()))
                    .collect(Collectors.toList());
        }

        List<ErrorCodeDto.ErrorCodeResponse> response = errorCodes.stream()
                .map(ErrorCodeDto.ErrorCodeResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Statistics endpoints
    @GetMapping("/statistics/count-by-project")
    public ResponseEntity<Object> getCountByProject() {
        var stats = errorCodeService.getAllProjects().stream()
                .collect(Collectors.toMap(
                        Project::getName,
                        project -> errorCodeService.getErrorCodesByProjectId(project.getId()).size()
                ));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/count-by-severity")
    public ResponseEntity<Object> getCountBySeverity() {
        var stats = errorCodeService.getAllErrorCodes().stream()
                .collect(Collectors.groupingBy(
                        ErrorCode::getSeverity,
                        Collectors.counting()
                ));
        return ResponseEntity.ok(stats);
    }
}