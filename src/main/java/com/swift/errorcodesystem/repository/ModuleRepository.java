package com.swift.errorcodesystem.repository;

import com.swift.errorcodesystem.entity.Module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCategoryId(Long categoryId);
    Optional<Module> findByCategoryIdAndCode(Long categoryId, String code);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.errorCodes WHERE m.id = :id")
    Optional<Module> findByIdWithErrorCodes(Long id);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.errorCodes WHERE m.category.id = :categoryId")
    List<Module> findByCategoryIdWithErrorCodes(Long categoryId);

    boolean existsByCategoryIdAndCode(Long categoryId, String code);
}
