package com.sese.translator.service;

import com.sese.translator.service.dto.LanguageDTO;
import com.sese.translator.service.dto.ProjectDTO;

import java.util.List;

/**
 * Service Interface for managing Project.
 */
public interface ProjectService {

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save
     * @return the persisted entity
     */
    ProjectDTO save(ProjectDTO projectDTO);

    LanguageDTO addLanguageToProject(ProjectDTO projectDTO, LanguageDTO languageDTO);

    LanguageDTO removeLanguageFromProject(ProjectDTO projectDTO, LanguageDTO languageDTO);

    /**
     *  Get all the projects of the current user.
     *
     *  @return the list of entities
     */
    List<ProjectDTO> findAllOfCurrentUser();

    /**
     *  Get the "id" project.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ProjectDTO findOne(Long id);

    /**
     *  Delete the "id" project.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
}
