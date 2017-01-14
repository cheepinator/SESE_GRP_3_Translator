package com.sese.translator.repository;

import com.sese.translator.domain.Definition;
import com.sese.translator.domain.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Definition entity.
 */
@SuppressWarnings("unused")
public interface DefinitionRepository extends JpaRepository<Definition, Long> {

    @Query("SELECT definition from Definition definition where definition.release.project.id = :id")
    Page<Definition> findByProjectId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT definition from Definition definition where definition.release.project.id = :id " +
        "and definition.release.versionTag = :versionTag")
    List<Definition> findByProjectIdAndVersionTag(@Param("id") Long id, @Param("versionTag") String versionTag);

    @Query("SELECT d FROM Definition d JOIN d.release r WHERE r.project.id=:projectId")
    List<Definition> definitionsOfProject(@Param("projectId") Long projectId);

}
