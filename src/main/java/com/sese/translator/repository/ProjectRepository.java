package com.sese.translator.repository;

import com.sese.translator.domain.Project;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Project entity.
 */
@SuppressWarnings("unused")
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("select project from Project project where project.owner.login = ?#{principal.username}")
    List<Project> findByOwnerIsCurrentUser();


}
