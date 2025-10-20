package com.swift.errorcodesystem.repository;

import com.swift.errorcodesystem.entity.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Long> {
    List<ErrorCode> findByModuleId(Long moduleId);
    Optional<ErrorCode> findByCode(String code);
    List<ErrorCode> findByHttpStatus(ErrorCode.HttpStatus httpStatus);
    List<ErrorCode> findBySeverity(ErrorCode.ErrorSeverity severity);

    @Query("SELECT ec FROM ErrorCode ec WHERE ec.module.category.project.id = :projectId")
    List<ErrorCode> findByProjectId(Long projectId);

    @Query("SELECT ec FROM ErrorCode ec WHERE ec.module.category.id = :categoryId")
    List<ErrorCode> findByCategoryId(Long categoryId);

    @Query("SELECT MAX(CAST(SUBSTRING(ec.code, 12, 4) AS int)) FROM ErrorCode ec WHERE ec.module.id = :moduleId")
    Optional<Integer> findMaxSequenceByModuleId(Long moduleId);

    boolean existsByCode(String code);
}