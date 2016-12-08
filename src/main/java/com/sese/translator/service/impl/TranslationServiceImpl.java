package com.sese.translator.service.impl;

import com.sese.translator.domain.Translation;
import com.sese.translator.repository.DefinitionRepository;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.NextTranslationDTO;
import com.sese.translator.service.dto.TranslationDTO;
import com.sese.translator.service.mapper.TranslationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;

/**
 * Service Implementation for managing Translation.
 */
@Service
@Transactional
public class TranslationServiceImpl implements TranslationService {

    private final Logger log = LoggerFactory.getLogger(TranslationServiceImpl.class);

    @Inject
    private TranslationRepository translationRepository;

    @Inject
    private DefinitionRepository definitionRepository;

    @Inject
    private TranslationMapper translationMapper;

    @Inject
    private UserService userService;

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

    @Override
    public TranslationDTO update(TranslationDTO translationDTO) {
        log.debug("Request to update Translation : {}", translationDTO);
        Translation translation = translationMapper.translationDTOToTranslation(translationDTO);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) auth.getPrincipal()).getUsername();
        com.sese.translator.domain.User user = userService.getUserWithAuthoritiesByLogin(username).orElseThrow(()
            -> new UsernameNotFoundException("User " + username + " was not found in the database"));
        translation.setTranslator(user);
        if (translation.getTranslatedText() != null) {

            translation.setUpdateNeeded(false);
        }

        translation = translationRepository.save(translation);
        TranslationDTO result = translationMapper.translationToTranslationDTO(translation);
        return result;
    }

    /**
     * Get all the translations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TranslationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Translations");
        Page<Translation> result = translationRepository.findAll(pageable);
        return result.map(translation -> translationMapper.translationToTranslationDTO(translation));
    }

    /**
     * Get one translation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public TranslationDTO findOne(Long id) {
        log.debug("Request to get Translation : {}", id);
        Translation translation = translationRepository.findOne(id);
        TranslationDTO translationDTO = translationMapper.translationToTranslationDTO(translation);
        return translationDTO;
    }

    /**
     * Get all translations for the given definition id
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public List<TranslationDTO> findForDefinition(Long id) {
        log.debug("Request to get Translations for definition with id: {}", id);
        List<Translation> byDefinitionId = translationRepository.findByDefinitionId(id);
        return translationMapper.translationsToTranslationDTOs(byDefinitionId);
    }

    /**
     * Get all translations for the given project id
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public List<TranslationDTO> findForProject(Long id) {
        log.debug("Request to get Translations for project with id: {}", id);
        List<Translation> byProjectId = translationRepository.findByProjectId(id);
        return translationMapper.translationsToTranslationDTOs(byProjectId);
    }

    @Override
    @Transactional
    public void markAllTranslationsForDefinitionAsUpdateNeeded(Long definitionId) {
        log.debug("Mark all translations for definition with id {} as 'update needed'", definitionId);
        List<Translation> translations = translationRepository.findByDefinitionId(definitionId);
        translations.forEach(translation -> translation.setUpdateNeeded(true));
        translationRepository.save(translations);
    }

    /**
     * Delete the  translation by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Translation : {}", id);
        translationRepository.delete(id);
    }

    @Override
    public TranslationDTO getNextOpenTranslation(NextTranslationDTO dto) {
        List<Translation> translations = translationRepository.findOpenTranslationsByReleaseAndLanguage(dto.getReleaseId(), dto.getLanguageId());
        if (!CollectionUtils.isEmpty(translations)) {//todo fill with translations
            return translationMapper.translationToTranslationDTO(translations.get((int) new Random().nextDouble() * translations.size()));
        } else {
            return null;
        }
    }
}
