package com.sese.translator.repository;

import com.sese.translator.domain.Project;
import com.sese.translator.domain.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Project entity.
 */
@SuppressWarnings("unused")
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("select distinct project from Project project left join fetch project.languages where project.owner.login = ?#{principal.username}")
    List<Project> findByOwnerIsCurrentUser();

    @Query("select distinct project from Project project left join fetch project.languages where project.owner.login = :username")
    List<Project> findByOwnerIsUser(@Param("username") String username);

    @Query("select project from Project project left join fetch project.languages where project.id =:id")
    Project findOneWithEagerRelationships(@Param("id") Long id);


}
