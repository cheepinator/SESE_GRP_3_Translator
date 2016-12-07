package com.sese.translator.repository;

import com.sese.translator.domain.Translation;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Translation entity.
 */
@SuppressWarnings("unused")
public interface TranslationRepository extends JpaRepository<Translation,Long> {

    @Query("select translation from Translation translation where translation.translator.login = ?#{principal.username}")
    List<Translation> findByTranslatorIsCurrentUser();

    @Query("select translation from Translation translation where translation.definition.id = :id")
    List<Translation> findByDefinitionId(@Param("id") Long id);

    @Query("select translation from Translation translation where translation.definition.release.project.id = :id")
    List<Translation> findByProjectId(@Param("id") Long id);

    @Query("select translation from Translation translation where translation.definition.release.project.id = :projectId " +
        "and translation.definition.release.versionTag = :versionTag " +
        "and translation.language.code = :languageCode")
    List<Translation> findByProjectIdLanguageIdReleaseId(@Param("projectId") Long projectId, @Param("versionTag") String versionTag, @Param("languageCode") String languageCode);

}
