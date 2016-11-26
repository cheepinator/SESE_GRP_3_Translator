package com.sese.translator.repository;

import com.sese.translator.domain.Translation;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Translation entity.
 */
@SuppressWarnings("unused")
public interface TranslationRepository extends JpaRepository<Translation,Long> {

    @Query("select translation from Translation translation where translation.translator.login = ?#{principal.username}")
    List<Translation> findByTranslatorIsCurrentUser();

}
