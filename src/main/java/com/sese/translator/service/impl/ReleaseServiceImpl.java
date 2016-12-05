package com.sese.translator.service.impl;

import com.sese.translator.domain.Project;
import com.sese.translator.domain.Release;
import com.sese.translator.repository.ReleaseRepository;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.service.dto.ProjectDTO;
import com.sese.translator.service.dto.ReleaseDTO;
import com.sese.translator.service.mapper.ProjectMapper;
import com.sese.translator.service.mapper.ReleaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Release.
 */
@Service
@Transactional
public class ReleaseServiceImpl implements ReleaseService{

    private final Logger log = LoggerFactory.getLogger(ReleaseServiceImpl.class);

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private ReleaseMapper releaseMapper;

    @Inject
    private ProjectMapper projectMapper;

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
        Release release = new Release().versionTag(Release.DEFAULT_TAG).project(project).isCurrentRelease(false);
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
     *  Get all the releases.
     *
     *  @return the list of entities
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
     *  Get one release by id.
     *
     *  @param id the id of the entity
     *  @return the entity
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


//    @Transactional(readOnly = true)
//    public ReleaseDTO findCurrentByProjectId(Long id) {
//        log.debug("Request to get current Release of Project : {}", id);
//        Release release = releaseRepository.findCurrentByProjectId(id);
//        ReleaseDTO releaseDTO = releaseMapper.releaseToReleaseDTO(release);
//        return releaseDTO;
//    }




    /**
     *  Delete the  release by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Release : {}", id);
        releaseRepository.delete(id);
    }
}
