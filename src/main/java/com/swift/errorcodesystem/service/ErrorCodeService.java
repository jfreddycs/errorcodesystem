package com.swift.errorcodesystem.service;


import com.swift.errorcodesystem.entity.Category;
import com.swift.errorcodesystem.entity.ErrorCode;
import com.swift.errorcodesystem.entity.Project;
import com.swift.errorcodesystem.entity.Module;
import com.swift.errorcodesystem.repository.CategoryRepository;
import com.swift.errorcodesystem.repository.ErrorCodeRepository;
import com.swift.errorcodesystem.repository.ModuleRepository;
import com.swift.errorcodesystem.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorCodeService {

    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final ModuleRepository moduleRepository;
    private final ErrorCodeRepository errorCodeRepository;

    // Project methods
    @Transactional
    public Project createProject(Project project) {
        if (projectRepository.existsByName(project.getName())) {
            throw new IllegalArgumentException("Project with name '" + project.getName() + "' already exists");
        }
        if (projectRepository.existsByCode(project.getCode())) {
            throw new IllegalArgumentException("Project with code '" + project.getCode() + "' already exists");
        }
        validateCodeFormat(project.getCode(), 2, "Project");
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Optional<Project> getProjectByIdWithCategories(Long id) {
        return projectRepository.findByIdWithCategories(id);
    }

    @Transactional
    public Project updateProject(Long id, Project projectDetails) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

        if (!project.getName().equals(projectDetails.getName()) &&
                projectRepository.existsByName(projectDetails.getName())) {
            throw new IllegalArgumentException("Project with name '" + projectDetails.getName() + "' already exists");
        }

        if (!project.getCode().equals(projectDetails.getCode()) &&
                projectRepository.existsByCode(projectDetails.getCode())) {
            throw new IllegalArgumentException("Project with code '" + projectDetails.getCode() + "' already exists");
        }

        validateCodeFormat(projectDetails.getCode(), 2, "Project");

        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setCode(projectDetails.getCode());
        project.setOwner(projectDetails.getOwner());
        project.setStatus(projectDetails.getStatus());

        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    // Category methods
    @Transactional
    public Category createCategory(Long projectId, Category category) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

        if (categoryRepository.existsByProjectIdAndCode(projectId, category.getCode())) {
            throw new IllegalArgumentException("Category with code '" + category.getCode() + "' already exists in this project");
        }

        validateCodeFormat(category.getCode(), 2, "Category");

        category.setProject(project);
        return categoryRepository.save(category);
    }

    public List<Category> getCategoriesByProjectId(Long projectId) {
        return categoryRepository.findByProjectId(projectId);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByIdWithModules(Long id) {
        return categoryRepository.findByIdWithModules(id);
    }

    // Module methods
    @Transactional
    public Module createModule(Long categoryId, Module module) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        if (moduleRepository.existsByCategoryIdAndCode(categoryId, module.getCode())) {
            throw new IllegalArgumentException("Module with code '" + module.getCode() + "' already exists in this category");
        }

        validateCodeFormat(module.getCode(), 2, "Module");

        module.setCategory(category);
        return moduleRepository.save(module);
    }

    public List<Module> getModulesByCategoryId(Long categoryId) {
        return moduleRepository.findByCategoryId(categoryId);
    }

    public Optional<Module> getModuleById(Long id) {
        return moduleRepository.findById(id);
    }

    public Optional<Module> getModuleByIdWithErrorCodes(Long id) {
        return moduleRepository.findByIdWithErrorCodes(id);
    }

    // Error Code methods
    @Transactional
    public ErrorCode createErrorCode(Long moduleId, ErrorCode errorCode) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with id: " + moduleId));

        // Generate the full error code
        String generatedCode = generateErrorCode(module);
        if (errorCodeRepository.existsByCode(generatedCode)) {
            throw new IllegalArgumentException("Error code '" + generatedCode + "' already exists");
        }

        errorCode.setCode(generatedCode);
        errorCode.setModule(module);
        return errorCodeRepository.save(errorCode);
    }

    private String generateErrorCode(Module module) {
        String projectCode = module.getCategory().getProject().getCode();
        String categoryCode = module.getCategory().getCode();
        String moduleCode = module.getCode();

        // Get the next sequence number for this module
        Integer maxSequence = errorCodeRepository.findMaxSequenceByModuleId(module.getId()).orElse(0);
        int nextSequence = maxSequence + 1;

        // Format: projectCode-categoryCode-moduleCode-sequence (01-05-15-0001)
        return String.format("%s-%s-%s-%04d", projectCode, categoryCode, moduleCode, nextSequence);
    }

    public List<ErrorCode> getErrorCodesByModuleId(Long moduleId) {
        return errorCodeRepository.findByModuleId(moduleId);
    }

    public List<ErrorCode> getErrorCodesByProjectId(Long projectId) {
        return errorCodeRepository.findByProjectId(projectId);
    }

    public List<ErrorCode> getErrorCodesByCategoryId(Long categoryId) {
        return errorCodeRepository.findByCategoryId(categoryId);
    }

    public Optional<ErrorCode> getErrorCodeById(Long id) {
        return errorCodeRepository.findById(id);
    }

    public Optional<ErrorCode> getErrorCodeByCode(String code) {
        return errorCodeRepository.findByCode(code);
    }

    public List<ErrorCode> getAllErrorCodes() {
        return errorCodeRepository.findAll();
    }

    @Transactional
    public ErrorCode updateErrorCode(Long id, ErrorCode errorCodeDetails) {
        ErrorCode errorCode = errorCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Error code not found with id: " + id));

        // Don't allow updating the code field as it's generated
        errorCode.setMessage(errorCodeDetails.getMessage());
        errorCode.setDescription(errorCodeDetails.getDescription());
        errorCode.setSuggestedAction(errorCodeDetails.getSuggestedAction());
        errorCode.setSeverity(errorCodeDetails.getSeverity());
        errorCode.setHttpStatus(errorCodeDetails.getHttpStatus());
        errorCode.setIsRetryable(errorCodeDetails.getIsRetryable());

        return errorCodeRepository.save(errorCode);
    }

    @Transactional
    public void deleteErrorCode(Long id) {
        if (!errorCodeRepository.existsById(id)) {
            throw new IllegalArgumentException("Error code not found with id: " + id);
        }
        errorCodeRepository.deleteById(id);
    }

    // Utility method for code validation
    private void validateCodeFormat(String code, int expectedLength, String entityName) {
        if (code == null || code.length() != expectedLength || !code.matches("\\d+")) {
            throw new IllegalArgumentException(entityName + " code must be " + expectedLength + " digits");
        }
    }

    // Search methods
    public List<ErrorCode> searchErrorCodes(String searchTerm) {
        return errorCodeRepository.findAll().stream()
                .filter(ec -> ec.getCode().contains(searchTerm) ||
                        ec.getMessage().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        ec.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }

    public List<ErrorCode> getErrorCodesBySeverity(ErrorCode.ErrorSeverity severity) {
        return errorCodeRepository.findBySeverity(severity);
    }

    public List<ErrorCode> getErrorCodesByHttpStatus(ErrorCode.HttpStatus httpStatus) {
        return errorCodeRepository.findByHttpStatus(httpStatus);
    }
}