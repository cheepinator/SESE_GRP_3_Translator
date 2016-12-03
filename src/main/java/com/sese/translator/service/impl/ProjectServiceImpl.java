package com.sese.translator.service.impl;

import com.sese.translator.domain.Project;
import com.sese.translator.repository.ProjectRepository;
import com.sese.translator.repository.ProjectassignmentRepository;
import com.sese.translator.service.ProjectService;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.ProjectDTO;
import com.sese.translator.service.dto.ProjectassignmentDTO;
import com.sese.translator.service.mapper.ProjectMapper;
import com.sese.translator.service.mapper.ProjectassignmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Project.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ProjectassignmentRepository projectassignmentRepository;

    @Inject
    private UserService userService;

    @Inject
    private ProjectMapper projectMapper;

    @Inject
    private ProjectassignmentMapper projectassignmentMapper;

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save
     * @return the persisted entity
     */
    public ProjectDTO save(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        Project project = projectMapper.projectDTOToProject(projectDTO);
        if (project.getOwner() == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = ((User) auth.getPrincipal()).getUsername();
            com.sese.translator.domain.User user = userService.getUserWithAuthoritiesByLogin(username).orElseThrow(()
                -> new UsernameNotFoundException("User " + username + " was not found in the database"));
            project.setOwner(user);
            log.debug("No user was set for project {}, set to '{}'", projectDTO, username);
        }
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
        HashSet<ProjectDTO> result = projectRepository
            .findByOwnerIsCurrentUser().stream()
            .map(projectMapper::projectToProjectDTO)
            .collect(Collectors.toCollection(HashSet::new));

        HashSet<ProjectassignmentDTO> resultProjectassignment =
            projectassignmentRepository.findByAssignedUserIsCurrentUser().stream()
                                       .map(projectassignmentMapper::projectassignmentToProjectassignmentDTO)
                                       .collect(Collectors.toCollection(HashSet::new));

        for (ProjectassignmentDTO projectassignmentDTO : resultProjectassignment) {
            result.add(findOne(projectassignmentDTO.getAssignedProjectId()));
        }

        return new ArrayList<ProjectDTO>(result);
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
