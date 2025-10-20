package com.swift.errorcodesystem.repository;


import com.swift.errorcodesystem.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
    Optional<Project> findByCode(String code);
    List<Project> findByStatus(Project.ProjectStatus status);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.categories WHERE p.id = :id")
    Optional<Project> findByIdWithCategories(Long id);

    boolean existsByName(String name);
    boolean existsByCode(String code);
}