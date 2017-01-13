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

    @Query("select translation from Translation translation where " +
        "translation.updateNeeded = true and " +
        "translation.definition.release.id = :releaseId and " +
        "translation.language.id = :languageId")
    List<Translation> findOpenTranslationsByReleaseAndLanguage(@Param("releaseId") Long releaseId, @Param("languageId") Long languageId);

    @Query("select translation from Translation translation where " +
        "translation.updateNeeded = true and " +
        "translation.language.id = :languageId")
    List<Translation> findOpenTranslationsByLanguage(@Param("languageId") Long languageId);

    @Query("select translation from Translation translation where translation.definition.release.project.id = :projectId " +
        "and translation.definition.release.versionTag = :versionTag " +
        "and translation.language.code = :languageCode")
    List<Translation> findByProjectIdLanguageIdReleaseId(@Param("projectId") Long projectId, @Param("versionTag") String versionTag, @Param("languageCode") String languageCode);


    //SELECT * FROM TRANSLATION JOIN DEFINITION ON TRANSLATION.DEFINITION_ID = DEFINITION.ID JOIN RELEASE ON DEFINITION.RELEASE_ID = RELEASE.ID WHERE RELEASE.PROJECT_ID = 1

    @Query("SELECT t FROM Translation t JOIN t.definition d JOIN d.release r WHERE r.project.id=:projectId")
    List<Translation> translationsOfProject(@Param("projectId") Long projectId);
}

