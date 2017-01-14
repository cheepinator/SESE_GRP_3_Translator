package com.sese.translator.repository;

import com.sese.translator.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Language entity.
 */
@SuppressWarnings("unused")
public interface LanguageRepository extends JpaRepository<Language,Long> {

    @Query("SELECT l from Language l join l.projects project where project.id = ?1")
    List<Language> findByProjectId(Long id);


}
