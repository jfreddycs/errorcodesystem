package com.swift.errorcodesystem.repository;


import com.swift.errorcodesystem.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByProjectId(Long projectId);
    Optional<Category> findByProjectIdAndCode(Long projectId, String code);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.modules WHERE c.id = :id")
    Optional<Category> findByIdWithModules(Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.modules WHERE c.project.id = :projectId")
    List<Category> findByProjectIdWithModules(Long projectId);

    boolean existsByProjectIdAndCode(Long projectId, String code);
}