package com.sese.translator.service.impl;

import com.sese.translator.domain.Definition;
import com.sese.translator.domain.Language;
import com.sese.translator.domain.Translation;
import com.sese.translator.repository.DefinitionRepository;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.service.DefinitionService;
import com.sese.translator.service.dto.DefinitionDTO;
import com.sese.translator.service.mapper.DefinitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

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

    @Inject
    private TranslationRepository translationRepository;

    /**
     * Save a definition.
     *
     * @param definitionDTO the entity to save
     * @return the persisted entity
     */
    public DefinitionDTO save(DefinitionDTO definitionDTO) {
        log.debug("Request to save Definition : {}", definitionDTO);
        Definition definition = definitionMapper.definitionDTOToDefinition(definitionDTO);
        //new definition with release
        definition = definitionRepository.save(definition);
        if(definition.getRelease() != null){
            for(Language lang : definition.getRelease().getLanguages()){
                if(!definition.getTranslations().stream().map(Translation::getLanguage).anyMatch(a->lang.equals(a))){
                    Translation translation = new Translation();
                    translation.setUpdateNeeded(true);
                    translation.setLanguage(lang);
                    translation.setDefinition(definition);
                    translationRepository.save(translation);

                }
            }
        }
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
     *  Get all the definitions for the given project.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DefinitionDTO> findAllForProject(Long projectId, Pageable pageable) {
        log.debug("Request to get all Definitions for project id {}", projectId);
        Page<Definition> result = definitionRepository.findByProjectId(projectId, pageable);
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
