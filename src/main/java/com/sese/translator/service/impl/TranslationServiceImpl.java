package com.sese.translator.service.impl;

import com.sese.translator.domain.Translation;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.dto.TranslationDTO;
import com.sese.translator.service.mapper.TranslationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Translation.
 */
@Service
@Transactional
public class TranslationServiceImpl implements TranslationService{

    private final Logger log = LoggerFactory.getLogger(TranslationServiceImpl.class);

    @Inject
    private TranslationRepository translationRepository;

    @Inject
    private TranslationMapper translationMapper;

    /**
     * Save a translation.
     *
     * @param translationDTO the entity to save
     * @return the persisted entity
     */
    public TranslationDTO save(TranslationDTO translationDTO) {
        log.debug("Request to save Translation : {}", translationDTO);
        Translation translation = translationMapper.translationDTOToTranslation(translationDTO);
        translation = translationRepository.save(translation);
        TranslationDTO result = translationMapper.translationToTranslationDTO(translation);
        return result;
    }

    /**
     *  Get all the translations.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TranslationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Translations");
        Page<Translation> result = translationRepository.findAll(pageable);
        return result.map(translation -> translationMapper.translationToTranslationDTO(translation));
    }

    /**
     *  Get one translation by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public TranslationDTO findOne(Long id) {
        log.debug("Request to get Translation : {}", id);
        Translation translation = translationRepository.findOne(id);
        TranslationDTO translationDTO = translationMapper.translationToTranslationDTO(translation);
        return translationDTO;
    }

    /**
     *  Get one translation by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public List<TranslationDTO> findForDefinition(Long id) {
        log.debug("Request to get Translations for definition with id: {}", id);
        List<Translation> byDefinitionId = translationRepository.findByDefinitionId(id);
        return translationMapper.translationsToTranslationDTOs(byDefinitionId);
    }

    /**
     *  Delete the  translation by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Translation : {}", id);
        translationRepository.delete(id);
    }
}
