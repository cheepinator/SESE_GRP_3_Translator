package com.sese.translator.service.impl;

import com.sese.translator.service.ProjectService;
import com.sese.translator.domain.Project;
import com.sese.translator.repository.ProjectRepository;
import com.sese.translator.service.dto.ProjectDTO;
import com.sese.translator.service.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Project.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService{

    private final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ProjectMapper projectMapper;

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save
     * @return the persisted entity
     */
    public ProjectDTO save(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        Project project = projectMapper.projectDTOToProject(projectDTO);
        project = projectRepository.save(project);
        ProjectDTO result = projectMapper.projectToProjectDTO(project);
        return result;
    }

    /**
     *  Get all the projects.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ProjectDTO> findAllOfCurrentUser() {
        log.debug("Request to get all Projects for current User");
        List<ProjectDTO> result = projectRepository.findByOwnerIsCurrentUser().stream()
            .map(projectMapper::projectToProjectDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one project by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public ProjectDTO findOne(Long id) {
        log.debug("Request to get Project : {}", id);
        Project project = projectRepository.findOne(id);
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);
        return projectDTO;
    }

    /**
     *  Delete the  project by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Project : {}", id);
        projectRepository.delete(id);
    }
}
