package com.sese.translator.service;

import com.sese.translator.service.dto.ProjectDTO;
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

    ReleaseDTO createDefaultRelease(ProjectDTO projectDTO);

    ReleaseDTO getDefaultReleaseForProject(Long projectId);

    /**
     *  Get all the releases.
     *
     *  @return the list of entities
     */
    List<ReleaseDTO> findAll();

    List<ReleaseDTO> findAllForProject(Long projectId);

    /**
     *  Get the "id" release.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ReleaseDTO findOne(Long id);

    List<ReleaseDTO> findAllForCurrentUser();
    /**
     *  Count All Translations in the current Release
     *
     *  @param id the id of the entity
     *  @return number of translations
     */
    Integer countTranslations(Long id);

    /**
     * Get the progress of an Release.
     * @param releaseId the release id.
     * @return the progress of the release btween 0 and 100%.
     */
    Double getReleaseProgress(Long releaseId);

    /**
     *  Delete the "id" release.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Find the active release of an project.
     * @param projectId the project id.
     * @return the active release.
     */
    ReleaseDTO findCurrentReleaseByProjectId(Long projectId);
}
