package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sese.translator.domain.Projectassignment;
import com.sese.translator.domain.User;
import com.sese.translator.repository.ProjectassignmentRepository;
import com.sese.translator.service.ProjectService;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.ProjectDTO;
import com.sese.translator.service.mapper.ProjectMapper;
import com.sese.translator.service.mapper.ProjectassignmentMapper;
import com.sese.translator.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing Project.
 */
@RestController
@RequestMapping("/api")
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectMapper projectMapper;

    @Inject
    private UserService userService;
    @Inject
    private ProjectassignmentRepository projectassignmentRepository;

    @Inject
    private ProjectassignmentMapper projectassignmentMapper;

    @Inject
    private ReleaseService releaseService;

    /**
     * POST  /projects : Create a new project.
     *
     * @param projectDTO the projectDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectDTO, or with status 400 (Bad Request) if the project has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/projects")
    @Timed
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        log.debug("REST request to save Project : {}", projectDTO);
        if (projectDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("project", "idexists", "A new project cannot already have an ID")).body(
                null);
        } else if (projectDTO.getOwnerId() != null) {
            return ResponseEntity.badRequest().headers(
                HeaderUtil.createFailureAlert("project", "ownerexists", "A new project cannot already have an Owner")).body(null);
        }
        ProjectDTO result = projectService.save(projectDTO);
        // create default release
        releaseService.createDefaultRelease(result);
        return ResponseEntity.created(new URI("/api/projects/" + result.getId()))
                             .headers(HeaderUtil.createEntityCreationAlert("project", result.getId().toString()))
                             .body(result);
    }

    /**
     * PUT  /projects : Updates an existing project.
     *
     * @param projectDTO the projectDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectDTO, or with status 400 (Bad Request) if the projectDTO is not valid, or
     * with status 500 (Internal Server Error) if the projectDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/projects")
    @Timed
    public ResponseEntity<ProjectDTO> updateProject(@Valid @RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        log.debug("REST request to update Project : {}", projectDTO);
        if (projectDTO.getId() == null) {
            return createProject(projectDTO);
        }
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert("project", projectDTO.getId().toString()))
                             .body(result);
    }

    /**
     * GET  /projects : get all the projects.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of projects in body
     */
    @GetMapping("/projects")
    @Timed
    public List<ProjectDTO> getAllProjectsOfCurrentUser() {
        log.debug("REST request to get all Projects of the current User");
        //return projectService.findAll();
        return projectService.findAllOfCurrentUser();
    }

    /**
     * GET  /projects/:id : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectDTO, or with status 404 (Not Found)
     */
    @GetMapping("/projects/{id}")
    @Timed
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        log.debug("REST request to get Project : {}", id);
        ProjectDTO projectDTO = projectService.findOne(id);
        return Optional.ofNullable(projectDTO)
                       .map(result -> new ResponseEntity<>(
                           result,
                           HttpStatus.OK))
                       .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * Get all Users of an Project.
     *
     * @param id of the project.
     * @return a List of Users.
     */
    @GetMapping("/projectusers/{id}")
    @Timed
    public List<User> getProjectUsers(@PathVariable Long id) {

        List<Projectassignment> projectassignments = projectassignmentRepository.findByAssignedProject(
            projectMapper.projectDTOToProject(projectService.findOne(id)));
        // not work with userMapper.userToUserDTO
        List<User> result = new ArrayList<>();

        for (Projectassignment projectassignment : projectassignments) {
            result.add(projectassignment.getAssignedUser());
        }

        return result;
    }

    /**
     * Get all Roles of the authentified user for a project.
     *
     * @param id of the project.
     * @return a List of Roles.
     */
    @GetMapping("projects/userRole/{id}")
    @Timed
    public List<String> getUserRoleForProject(@PathVariable Long id) {

        List<Projectassignment> projectassignments = projectassignmentRepository.findByAssignedUser(userService.getUserWithAuthorities());

        List<String> result = new ArrayList<>();

        for (Projectassignment projectassignment : projectassignments) {
            if (Objects.equals(projectassignment.getAssignedProject().getId(), id)) {
                result.add(projectassignment.getRole().name());
            }
        }

        return result;
    }

    /**
     * DELETE  /projects/:id : delete the "id" project.
     *
     * @param id the id of the projectDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/projects/{id}")
    @Timed
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.debug("REST request to delete Project : {}", id);
        projectService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("project", id.toString())).build();
    }
}
