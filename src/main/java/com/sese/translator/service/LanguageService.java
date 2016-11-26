package com.sese.translator.service;

import com.sese.translator.service.dto.LanguageDTO;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Language.
 */
public interface LanguageService {

    /**
     * Save a language.
     *
     * @param languageDTO the entity to save
     * @return the persisted entity
     */
    LanguageDTO save(LanguageDTO languageDTO);

    /**
     *  Get all the languages.
     *  
     *  @return the list of entities
     */
    List<LanguageDTO> findAll();

    /**
     *  Get the "id" language.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    LanguageDTO findOne(Long id);

    /**
     *  Delete the "id" language.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
}
