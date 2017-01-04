package com.sese.translator.service;

import com.sese.translator.domain.Definition;
import com.sese.translator.service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Interface for managing Translation.
 */
public interface TranslationService {

    /**
     * Save a translation.
     *
     * @param translationDTO the entity to save
     * @return the persisted entity
     */
    TranslationDTO save(TranslationDTO translationDTO);

    /**
     * Updates a translation.
     *
     * @param translationDTO the entity to update
     * @return the persisted entity
     */
    TranslationDTO update(TranslationDTO translationDTO);

    /**
     *  Get all the translations.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<TranslationDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" translation.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    TranslationDTO findOne(Long id);

    List<TranslationDTO> findForDefinition(Long id);

    List<TranslationDTO> findForProject(Long id);

    void markAllTranslationsForDefinitionAsUpdateNeeded(Long definitionId);

    void addMissingTranslationsForProjectAndLanguage(ProjectDTO projectDTO, LanguageDTO languageDTO);

    void addMissingTranslationsToDefinition(Definition definition);

    @Transactional
    List<ProgressDTO> getProgressForProject(ProjectDTO projectDTO);

    void removeAllTranslationsForProjectAndLanguage(ProjectDTO projectDTO, LanguageDTO languageDTO);

    /**
     *  Delete the "id" translation.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);


    /**
     *  Get the next open translation for a
     *  release and language
     *
     *  @param dto the request Object
     */
    List<TranslationDTO> getNextOpenTranslation(NextTranslationDTO dto);

}
