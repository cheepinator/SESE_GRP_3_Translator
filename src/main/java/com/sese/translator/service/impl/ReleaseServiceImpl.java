package com.sese.translator.service.impl;

import com.sese.translator.domain.Definition;
import com.sese.translator.domain.Language;
import com.sese.translator.domain.Project;
import com.sese.translator.domain.Release;
import com.sese.translator.domain.Translation;
import com.sese.translator.repository.ReleaseRepository;
import com.sese.translator.service.LanguageService;
import com.sese.translator.service.ProjectService;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.service.dto.LanguageDTO;
import com.sese.translator.service.dto.ProjectDTO;
import com.sese.translator.service.dto.ReleaseDTO;
import com.sese.translator.service.mapper.LanguageMapper;
import com.sese.translator.service.mapper.ProjectMapper;
import com.sese.translator.service.mapper.ReleaseMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Release.
 */
@Service
@Transactional
public class ReleaseServiceImpl implements ReleaseService {

    private final Logger log = LoggerFactory.getLogger(ReleaseServiceImpl.class);
    @Inject
    private ReleaseRepository releaseRepository;
    @Inject
    private LanguageService languageService;
    @Inject
    private LanguageMapper languageMapper;
    @Inject
    private ReleaseMapper releaseMapper;
    @Inject
    private ProjectMapper projectMapper;
    @Inject
    private ProjectService projectService;

    /**
     * Save a release.
     *
     * @param releaseDTO the entity to save
     * @return the persisted entity
     */
    public ReleaseDTO save(ReleaseDTO releaseDTO) {
        log.debug("Request to save Release : {}", releaseDTO);
        Release release = releaseMapper.releaseDTOToRelease(releaseDTO);
        release = releaseRepository.save(release);
        ReleaseDTO result = releaseMapper.releaseToReleaseDTO(release);
        return result;
    }

    @Override
    public ReleaseDTO createDefaultRelease(ProjectDTO projectDTO) {
        log.debug("Creating default release for Project: {}", projectDTO);
        Project project = projectMapper.projectDTOToProject(projectDTO);
        Release release = new Release().versionTag(Release.DEFAULT_TAG).project(project);
        release = releaseRepository.save(release);
        ReleaseDTO result = releaseMapper.releaseToReleaseDTO(release);
        log.debug("Default release: {}", result);
        return result;
    }

    @Override
    public ReleaseDTO getDefaultReleaseForProject(Long projectId) {
        Release defaultForProject = releaseRepository.findDefaultForProject(projectId);
        ReleaseDTO releaseDTO = releaseMapper.releaseToReleaseDTO(defaultForProject);
        log.debug("Returning default release for project {}: {}", projectId, releaseDTO);
        return releaseDTO;
    }

    /**
     * Get all the releases.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ReleaseDTO> findAll() {
        log.debug("Request to get all Releases");
        List<ReleaseDTO> result = releaseRepository.findAllWithEagerRelationships().stream()
                                                   .map(releaseMapper::releaseToReleaseDTO)
                                                   .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }


    /**
     * Get all the releases for the current owner .
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ReleaseDTO> findAllForCurrentUser() {
        log.debug("Request to get all Releases for CurrentUser");
        List<ReleaseDTO> result = releaseRepository.findByOwnerIsCurrentUser().stream()
                                                   .map(releaseMapper::releaseToReleaseDTO)
                                                   .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     * Get all the releases for the given project id.
     *
     * @return the list of entities for the project
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReleaseDTO> findAllForProject(Long projectId) {
        log.debug("Request to get all Releases for Project with id: {}", projectId);
        return releaseRepository.findByProjectIdWithEagerRelationships(projectId).stream()
                                .map(releaseMapper::releaseToReleaseDTO)
                                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one release by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ReleaseDTO findOne(Long id) {
        log.debug("Request to get Release : {}", id);
        Release release = releaseRepository.findOneWithEagerRelationships(id);
        ReleaseDTO releaseDTO = releaseMapper.releaseToReleaseDTO(release);
        return releaseDTO;
    }

    /**
     * Count All Translations in the current Release
     *
     * @param id the id of the entity
     * @return number of translations
     */
    @Transactional(readOnly = true)
    public Integer countTranslations(Long id) {
        log.debug("Request count translations for Release : {}", id);
        return releaseRepository.countByReleaseId(id);
    }

    @Transactional(readOnly = true)
    public Double getReleaseProgress(Long releaseId) {

        Release release = releaseRepository.findOne(releaseId);
        Project project = release.getProject();
        Set<Definition> definitions = release.getDefinitions();
        Set<Language> languages = project.getLanguages();

        double toBeTranslatedCount = definitions.size() * languages.size();
        double translatedDefinitions = 0;

        for (Definition definition : definitions) {
            for (Translation translation : definition.getTranslations()) {
                if (translation.getTranslatedText() != null && !translation.getTranslatedText().equals("") && !translation.isUpdateNeeded()) {
                    translatedDefinitions++;
                }
            }
        }

        BigDecimal bd;
        if (toBeTranslatedCount > 0) {
            bd = new BigDecimal((translatedDefinitions / (toBeTranslatedCount / 100.0)));
        } else {
            bd = new BigDecimal(0);
        }
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    @Transactional(readOnly = true)
    public ReleaseDTO findCurrentReleaseByProjectId(Long projectId) {
        log.debug("Request to get current Release of Project : {}", projectId);
        final long now = System.currentTimeMillis();
        Date nowDate = new Date();
        List<Date> dates = new ArrayList<>();
        ProjectDTO projectDTO = projectService.findOne(projectId);
        List<ReleaseDTO> releases = findAllForProject(projectDTO.getId());

        //edge case when only the default release is available
        if (releases.size() == 1 && releases.get(0).getVersionTag().equals(Release.DEFAULT_TAG)) {
            return releases.get(0);
        }

        for (ReleaseDTO release : releases) {
            if (release.getDueDate() != null) {
                Date d = Date.from(release.getDueDate().toInstant());
                if (d.after(nowDate)) {
                    dates.add(d);
                }
            }
        }
        Date closest = new Date();
        if (dates.size() > 0) {
            closest = Collections.min(dates, new Comparator<Date>() {
                public int compare(Date d1, Date d2) {
                    long diff1 = Math.abs(d1.getTime() - now);
                    long diff2 = Math.abs(d2.getTime() - now);
                    return Long.compare(diff1, diff2);
                }
            });
        }
        ZonedDateTime closestInZonedDateTime = ZonedDateTime.ofInstant(closest.toInstant(), ZoneId.systemDefault());

        Comparator<ZonedDateTime> comparator = Comparator.comparing(
            zdt -> zdt.truncatedTo(ChronoUnit.MINUTES));

        ReleaseDTO releaseDTO = null;
        for (ReleaseDTO release : releases) {
            if (release.getDueDate() != null) {
                if (comparator.compare(closestInZonedDateTime, release.getDueDate()) == 0) {
                    releaseDTO = release;
                }
            }
        }

        return releaseDTO;
    }


    /**
     * Delete the  release by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Release : {}", id);
        releaseRepository.delete(id);
    }

    private Language getDefaultLanguage() { // todo: refactor to new logic
        boolean hasGermanLanguage = false;
        Long languageId = 0L;
        for (LanguageDTO languageDTO : languageService.findAll()) {
            if (languageDTO.getCode().equals(Language.DEFAULT_LANGUAGE)) {
                hasGermanLanguage = true;
                languageId = languageDTO.getId();
            }
        }

        if (!hasGermanLanguage) {
            LanguageDTO defaultLanguage = new LanguageDTO();
            defaultLanguage.setCode(Language.DEFAULT_LANGUAGE);
            languageId = languageService.save(defaultLanguage).getId();
        }

        return languageMapper.languageDTOToLanguage(languageService.findOne(languageId));
    }
}
