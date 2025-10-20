package com.swift.errorcodesystem.dto;

import com.swift.errorcodesystem.entity.Category;
import com.swift.errorcodesystem.entity.ErrorCode;
import com.swift.errorcodesystem.entity.Project;
import com.swift.errorcodesystem.entity.Module;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorCodeDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProjectRequest {
        @NotBlank(message = "Project name is required")
        @Size(max = 100, message = "Project name must not exceed 100 characters")
        private String name;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        @NotBlank(message = "Project code is required")
        @Pattern(regexp = "\\d{2}", message = "Project code must be exactly 2 digits")
        private String code;

        @NotBlank(message = "Owner is required")
        @Size(max = 50, message = "Owner must not exceed 50 characters")
        private String owner;

        @NotNull(message = "Status is required")
        private Project.ProjectStatus status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProjectResponse {
        private Long id;
        private String name;
        private String description;
        private String code;
        private String owner;
        private Project.ProjectStatus status;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;

        private List<CategoryResponse> categories;

        public static ProjectResponse fromEntity(Project project) {
            return ProjectResponse.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .description(project.getDescription())
                    .code(project.getCode())
                    .owner(project.getOwner())
                    .status(project.getStatus())
                    .createdAt(project.getCreatedAt())
                    .updatedAt(project.getUpdatedAt())
                    .categories(project.getCategories() != null ?
                            project.getCategories().stream()
                                    .map(CategoryResponse::fromEntity)
                                    .collect(Collectors.toList()) : null)
                    .build();
        }

        public static ProjectResponse fromEntityWithoutCategories(Project project) {
            return ProjectResponse.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .description(project.getDescription())
                    .code(project.getCode())
                    .owner(project.getOwner())
                    .status(project.getStatus())
                    .createdAt(project.getCreatedAt())
                    .updatedAt(project.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCategoryRequest {
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        private String name;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        @NotBlank(message = "Category code is required")
        @Pattern(regexp = "\\d{2}", message = "Category code must be exactly 2 digits")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
        private String code;
        private Long projectId;
        private String projectName;
        private String projectCode;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;

        private List<ModuleResponse> modules;

        public static CategoryResponse fromEntity(Category category) {
            return CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .code(category.getCode())
                    .projectId(category.getProject().getId())
                    .projectName(category.getProject().getName())
                    .projectCode(category.getProject().getCode())
                    .createdAt(category.getCreatedAt())
                    .updatedAt(category.getUpdatedAt())
                    .modules(category.getModules() != null ?
                            category.getModules().stream()
                                    .map(ModuleResponse::fromEntity)
                                    .collect(Collectors.toList()) : null)
                    .build();
        }

        public static CategoryResponse fromEntityWithoutModules(Category category) {
            return CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .code(category.getCode())
                    .projectId(category.getProject().getId())
                    .projectName(category.getProject().getName())
                    .projectCode(category.getProject().getCode())
                    .createdAt(category.getCreatedAt())
                    .updatedAt(category.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateModuleRequest {
        @NotBlank(message = "Module name is required")
        @Size(max = 100, message = "Module name must not exceed 100 characters")
        private String name;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        @NotBlank(message = "Module code is required")
        @Pattern(regexp = "\\d{2}", message = "Module code must be exactly 2 digits")
        private String code;

        @NotBlank(message = "Purpose is required")
        @Size(max = 1000, message = "Purpose must not exceed 1000 characters")
        private String purpose;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ModuleResponse {
        private Long id;
        private String name;
        private String description;
        private String code;
        private String purpose;
        private Long categoryId;
        private String categoryName;
        private String categoryCode;
        private Long projectId;
        private String projectName;
        private String projectCode;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;

        private List<ErrorCodeResponse> errorCodes;

        public static ModuleResponse fromEntity(Module module) {
            return ModuleResponse.builder()
                    .id(module.getId())
                    .name(module.getName())
                    .description(module.getDescription())
                    .code(module.getCode())
                    .purpose(module.getPurpose())
                    .categoryId(module.getCategory().getId())
                    .categoryName(module.getCategory().getName())
                    .categoryCode(module.getCategory().getCode())
                    .projectId(module.getCategory().getProject().getId())
                    .projectName(module.getCategory().getProject().getName())
                    .projectCode(module.getCategory().getProject().getCode())
                    .createdAt(module.getCreatedAt())
                    .updatedAt(module.getUpdatedAt())
                    .errorCodes(module.getErrorCodes() != null ?
                            module.getErrorCodes().stream()
                                    .map(ErrorCodeResponse::fromEntity)
                                    .collect(Collectors.toList()) : null)
                    .build();
        }

        public static ModuleResponse fromEntityWithoutErrorCodes(Module module) {
            return ModuleResponse.builder()
                    .id(module.getId())
                    .name(module.getName())
                    .description(module.getDescription())
                    .code(module.getCode())
                    .purpose(module.getPurpose())
                    .categoryId(module.getCategory().getId())
                    .categoryName(module.getCategory().getName())
                    .categoryCode(module.getCategory().getCode())
                    .projectId(module.getCategory().getProject().getId())
                    .projectName(module.getCategory().getProject().getName())
                    .projectCode(module.getCategory().getProject().getCode())
                    .createdAt(module.getCreatedAt())
                    .updatedAt(module.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateErrorCodeRequest {
        @NotBlank(message = "Message is required")
        @Size(max = 200, message = "Message must not exceed 200 characters")
        private String message;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @Size(max = 500, message = "Suggested action must not exceed 500 characters")
        private String suggestedAction;

        @NotNull(message = "Severity is required")
        private ErrorCode.ErrorSeverity severity;

        @NotNull(message = "HTTP status is required")
        private ErrorCode.HttpStatus httpStatus;

        @NotNull(message = "Retryable flag is required")
        private Boolean isRetryable;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorCodeResponse {
        private Long id;
        private String code; // Full code: 01-05-15-0001
        private String message;
        private String description;
        private String suggestedAction;
        private ErrorCode.ErrorSeverity severity;
        private ErrorCode.HttpStatus httpStatus;
        private Integer httpStatusCode;
        private Boolean isRetryable;
        private Long moduleId;
        private String moduleName;
        private String moduleCode;
        private Long categoryId;
        private String categoryName;
        private String categoryCode;
        private Long projectId;
        private String projectName;
        private String projectCode;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;

        public static ErrorCodeResponse fromEntity(ErrorCode errorCode) {
            return ErrorCodeResponse.builder()
                    .id(errorCode.getId())
                    .code(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .description(errorCode.getDescription())
                    .suggestedAction(errorCode.getSuggestedAction())
                    .severity(errorCode.getSeverity())
                    .httpStatus(errorCode.getHttpStatus())
                    .httpStatusCode(errorCode.getHttpStatusCode())
                    .isRetryable(errorCode.getIsRetryable())
                    .moduleId(errorCode.getModule().getId())
                    .moduleName(errorCode.getModule().getName())
                    .moduleCode(errorCode.getModule().getCode())
                    .categoryId(errorCode.getModule().getCategory().getId())
                    .categoryName(errorCode.getModule().getCategory().getName())
                    .categoryCode(errorCode.getModule().getCategory().getCode())
                    .projectId(errorCode.getModule().getCategory().getProject().getId())
                    .projectName(errorCode.getModule().getCategory().getProject().getName())
                    .projectCode(errorCode.getModule().getCategory().getProject().getCode())
                    .createdAt(errorCode.getCreatedAt())
                    .updatedAt(errorCode.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequest {
        private String searchTerm;
        private ErrorCode.ErrorSeverity severity;
        private ErrorCode.HttpStatus httpStatus;
        private String projectCode;
        private String categoryCode;
        private String moduleCode;
        private Boolean isRetryable;
    }
}