package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sese.translator.domain.Projectassignment;

import com.sese.translator.repository.ProjectassignmentRepository;
import com.sese.translator.web.rest.util.HeaderUtil;
import com.sese.translator.service.dto.ProjectassignmentDTO;
import com.sese.translator.service.mapper.ProjectassignmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Projectassignment.
 */
@RestController
@RequestMapping("/api")
public class ProjectassignmentResource {

    private final Logger log = LoggerFactory.getLogger(ProjectassignmentResource.class);

    @Inject
    private ProjectassignmentRepository projectassignmentRepository;

    @Inject
    private ProjectassignmentMapper projectassignmentMapper;

    /**
     * POST  /projectassignments : Create a new projectassignment.
     *
     * @param projectassignmentDTO the projectassignmentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectassignmentDTO, or with status 400 (Bad Request) if the projectassignment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/projectassignments")
    @Timed
    public ResponseEntity<ProjectassignmentDTO> createProjectassignment(@Valid @RequestBody ProjectassignmentDTO projectassignmentDTO) throws URISyntaxException {
        log.debug("REST request to save Projectassignment : {}", projectassignmentDTO);
        if (projectassignmentDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("projectassignment", "idexists", "A new projectassignment cannot already have an ID")).body(null);
        }
        Projectassignment projectassignment = projectassignmentMapper.projectassignmentDTOToProjectassignment(projectassignmentDTO);
        projectassignment = projectassignmentRepository.save(projectassignment);
        ProjectassignmentDTO result = projectassignmentMapper.projectassignmentToProjectassignmentDTO(projectassignment);
        return ResponseEntity.created(new URI("/api/projectassignments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("projectassignment", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /projectassignments : Updates an existing projectassignment.
     *
     * @param projectassignmentDTO the projectassignmentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectassignmentDTO,
     * or with status 400 (Bad Request) if the projectassignmentDTO is not valid,
     * or with status 500 (Internal Server Error) if the projectassignmentDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/projectassignments")
    @Timed
    public ResponseEntity<ProjectassignmentDTO> updateProjectassignment(@Valid @RequestBody ProjectassignmentDTO projectassignmentDTO) throws URISyntaxException {
        log.debug("REST request to update Projectassignment : {}", projectassignmentDTO);
        if (projectassignmentDTO.getId() == null) {
            return createProjectassignment(projectassignmentDTO);
        }
        Projectassignment projectassignment = projectassignmentMapper.projectassignmentDTOToProjectassignment(projectassignmentDTO);
        projectassignment = projectassignmentRepository.save(projectassignment);
        ProjectassignmentDTO result = projectassignmentMapper.projectassignmentToProjectassignmentDTO(projectassignment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("projectassignment", projectassignmentDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /projectassignments : get all the projectassignments from the users own Projects.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of projectassignments in body
     */
    @GetMapping("/projectassignments")
    @Timed
    public List<ProjectassignmentDTO> getAllProjectassignments() {
        log.debug("REST request to get all Projectassignments");
        List<Projectassignment> projectassignments = projectassignmentRepository.findByAssignedProjectBelongToCurrentUser();
        return projectassignmentMapper.projectassignmentsToProjectassignmentDTOs(projectassignments);
    }

    /**
     * GET  /projectassignments/:id : get the "id" projectassignment.
     *
     * @param id the id of the projectassignmentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectassignmentDTO, or with status 404 (Not Found)
     */
    @GetMapping("/projectassignments/{id}")
    @Timed
    public ResponseEntity<ProjectassignmentDTO> getProjectassignment(@PathVariable Long id) {
        log.debug("REST request to get Projectassignment : {}", id);
        Projectassignment projectassignment = projectassignmentRepository.findOne(id);
        ProjectassignmentDTO projectassignmentDTO = projectassignmentMapper.projectassignmentToProjectassignmentDTO(projectassignment);
        return Optional.ofNullable(projectassignmentDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /projectassignments/:id : delete the "id" projectassignment.
     *
     * @param id the id of the projectassignmentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/projectassignments/{id}")
    @Timed
    public ResponseEntity<Void> deleteProjectassignment(@PathVariable Long id) {
        log.debug("REST request to delete Projectassignment : {}", id);
        projectassignmentRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectassignment", id.toString())).build();
    }

}
