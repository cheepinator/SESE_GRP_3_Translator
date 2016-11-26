package com.sese.translator.service.impl;

import com.sese.translator.service.DefinitionService;
import com.sese.translator.domain.Definition;
import com.sese.translator.repository.DefinitionRepository;
import com.sese.translator.service.dto.DefinitionDTO;
import com.sese.translator.service.mapper.DefinitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Definition.
 */
@Service
@Transactional
public class DefinitionServiceImpl implements DefinitionService{

    private final Logger log = LoggerFactory.getLogger(DefinitionServiceImpl.class);
    
    @Inject
    private DefinitionRepository definitionRepository;

    @Inject
    private DefinitionMapper definitionMapper;

    /**
     * Save a definition.
     *
     * @param definitionDTO the entity to save
     * @return the persisted entity
     */
    public DefinitionDTO save(DefinitionDTO definitionDTO) {
        log.debug("Request to save Definition : {}", definitionDTO);
        Definition definition = definitionMapper.definitionDTOToDefinition(definitionDTO);
        definition = definitionRepository.save(definition);
        DefinitionDTO result = definitionMapper.definitionToDefinitionDTO(definition);
        return result;
    }

    /**
     *  Get all the definitions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<DefinitionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Definitions");
        Page<Definition> result = definitionRepository.findAll(pageable);
        return result.map(definition -> definitionMapper.definitionToDefinitionDTO(definition));
    }

    /**
     *  Get one definition by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public DefinitionDTO findOne(Long id) {
        log.debug("Request to get Definition : {}", id);
        Definition definition = definitionRepository.findOne(id);
        DefinitionDTO definitionDTO = definitionMapper.definitionToDefinitionDTO(definition);
        return definitionDTO;
    }

    /**
     *  Delete the  definition by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Definition : {}", id);
        definitionRepository.delete(id);
    }
}
