package com.sese.translator.service;

import com.sese.translator.service.dto.ReleaseDTO;

import java.util.List;

/**
 * Service Interface for managing Release.
 */
public interface ReleaseService {

    /**
     * Save a release.
     *
     * @param releaseDTO the entity to save
     * @return the persisted entity
     */
    ReleaseDTO save(ReleaseDTO releaseDTO);

    /**
     *  Get all the releases.
     *
     *  @return the list of entities
     */
    List<ReleaseDTO> findAll();

    /**
     *  Get the "id" release.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ReleaseDTO findOne(Long id);


    /**
     *  Count All Translations in the current Release
     *
     *  @param id the id of the entity
     *  @return number of translations
     */
    Integer countTranslations(Long id);

    /**
     *  Delete the "id" release.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);



}
