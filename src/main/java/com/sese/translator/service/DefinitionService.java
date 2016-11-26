package com.sese.translator.service;

import com.sese.translator.service.dto.DefinitionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Definition.
 */
public interface DefinitionService {

    /**
     * Save a definition.
     *
     * @param definitionDTO the entity to save
     * @return the persisted entity
     */
    DefinitionDTO save(DefinitionDTO definitionDTO);

    /**
     *  Get all the definitions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<DefinitionDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" definition.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    DefinitionDTO findOne(Long id);

    /**
     *  Delete the "id" definition.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
}
