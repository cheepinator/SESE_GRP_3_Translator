package com.sese.translator.service.impl;

import com.sese.translator.domain.*;
import com.sese.translator.repository.DefinitionRepository;
import com.sese.translator.repository.ReleaseRepository;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.*;
import com.sese.translator.service.mapper.LanguageMapper;
import com.sese.translator.service.mapper.ProjectMapper;
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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    private ReleaseRepository releaseRepository;

    @Inject
    private TranslationMapper translationMapper;

    @Inject
    private UserService userService;

    @Inject
    private LanguageMapper languageMapper;

    @Inject
    private ProjectMapper projectMapper;

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

    @Override
    @Transactional
    public void addMissingTranslationsForProjectAndLanguage(ProjectDTO projectDTO, LanguageDTO languageDTO) {
        log.debug("Adding missing translations for project with id {} and new language {}", projectDTO.getId(),
            languageDTO.getCode());
        Language language = languageMapper.languageDTOToLanguage(languageDTO);
        Page<Definition> projectDefinitions = definitionRepository.findByProjectId(projectDTO.getId(), null);
        projectDefinitions.getContent().forEach(definition -> addMissingTranslationsToDefinition(definition, language));
    }

    @Override
    @Transactional
    public void addMissingTranslationsToDefinition(Definition definition) {
        Set<Language> alreadyTranslatedLanguages = getAlreadyTranslatedLanguages(definition);
        // fetch the release manually to avoid non fetched data problems
        Release release = releaseRepository.findOne(definition.getRelease().getId());
        for (Language projectLanguage : release.getProject().getLanguages()) {
            if (!alreadyTranslatedLanguages.contains(projectLanguage)) {
                Translation translation = new Translation()
                    .updateNeeded(true)
                    .language(projectLanguage)
                    .definition(definition);
                translationRepository.save(translation);
                log.debug("Added new empty translation for definition with id {} and language {}", definition.getId(),
                    projectLanguage.getCode());
            }
        }
    }

    private Set<Language> getAlreadyTranslatedLanguages(Definition definition) {
        return definition.getTranslations().stream()
                         .map(Translation::getLanguage)
                         .collect(Collectors.toSet());
    }

    private void addMissingTranslationsToDefinition(Definition definition, Language newLanguage) {
        if (isNotAlreadyTranslated(definition, newLanguage)) {
            Translation translation = new Translation()
                .updateNeeded(true)
                .language(newLanguage)
                .definition(definition);
            translationRepository.save(translation);
            log.debug("Added new empty translation for definition with id {} and language {}", definition.getId(),
                newLanguage.getCode());
        }
    }

    private boolean isNotAlreadyTranslated(Definition definition, Language newLanguage) {
        return definition.getTranslations().stream()
                         .map(Translation::getLanguage)
                         .noneMatch(availableLanguage -> availableLanguage.equals(newLanguage));
    }

    @Override
    @Transactional
    public List<ProgressDTO> getProgressForProject(ProjectDTO projectDTO) {
        Page<Definition> definitionsPage = definitionRepository.findByProjectId(projectDTO.getId(), null);
        List<Definition> definitions = definitionsPage.getContent();
        Project project = projectMapper.projectDTOToProject(projectDTO);
        ArrayList<ProgressDTO> result = new ArrayList<>();
        for (Language language : project.getLanguages()) {
            LanguageDTO languageDTO = languageMapper.languageToLanguageDTO(language);
            long count = definitions.stream()
                                    .flatMap(definition -> definition.getTranslations().stream())
                                    .filter(translation -> !translation.isUpdateNeeded())
                                    .map(Translation::getLanguage)
                                    .filter(Predicate.isEqual(language))
                                    .count();

            ProgressDTO progressDTO = new ProgressDTO(languageDTO, ((long) definitions.size()), count);
            result.add(progressDTO);
        }
        return result;
    }

    @Override
    @Transactional
    public void removeAllTranslationsForProjectAndLanguage(ProjectDTO projectDTO, LanguageDTO languageDTO) {
        log.debug("Remove translations for project with id {} and language {}", projectDTO.getId(),
            languageDTO.getCode());
        Page<Definition> projectDefinitions = definitionRepository.findByProjectId(projectDTO.getId(), null);
        projectDefinitions.getContent().forEach(definition -> removeTranslationsForDefinition(definition, languageDTO));
    }

    private void removeTranslationsForDefinition(Definition definition, LanguageDTO languageDTO) {
        ArrayList<Translation> translations = new ArrayList<>(definition.getTranslations());
        for (Translation translation : translations) {
            if (Objects.equals(translation.getLanguage().getId(), languageDTO.getId())) {
                definition.removeTranslations(translation);
                translationRepository.delete(translation);
                log.debug("Removed translation for definition with id {} and language {}", definition.getId(), languageDTO.getCode());
            }
        }
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
    @Transactional
    public List<TranslationDTO> getNextOpenTranslation(NextTranslationDTO dto) {
        if (dto.getReleaseId() == null) {
            return translationMapper.translationsToTranslationDTOs(translationRepository.findOpenTranslationsByLanguage(dto.getLanguageId()));
        } else {
            return translationMapper.translationsToTranslationDTOs(translationRepository.findOpenTranslationsByReleaseAndLanguage(dto.getReleaseId(), dto.getLanguageId()));
        }
    }
}
