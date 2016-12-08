package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sese.translator.service.ProjectService;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.service.dto.ProjectDTO;
import com.sese.translator.service.dto.ReleaseDTO;
import com.sese.translator.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Release.
 */
@RestController
@RequestMapping("/api")
public class ReleaseResource {

    private final Logger log = LoggerFactory.getLogger(ReleaseResource.class);

    @Inject
    private ReleaseService releaseService;

    @Inject
    private ProjectService projectService;

    /**
     * POST  /releases : Create a new release.
     *
     * @param releaseDTO the releaseDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new releaseDTO, or with status 400 (Bad Request) if the release has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/releases")
    @Timed
    public ResponseEntity<ReleaseDTO> createRelease(@Valid @RequestBody ReleaseDTO releaseDTO) throws URISyntaxException {
        log.debug("REST request to save Release : {}", releaseDTO);
        if (releaseDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("release", "idexists", "A new release cannot already have an ID")).body(null);
        }
        ReleaseDTO result = releaseService.save(releaseDTO);
        return ResponseEntity.created(new URI("/api/releases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("release", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /releases : Updates an existing release.
     *
     * @param releaseDTO the releaseDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated releaseDTO,
     * or with status 400 (Bad Request) if the releaseDTO is not valid,
     * or with status 500 (Internal Server Error) if the releaseDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/releases")
    @Timed
    public ResponseEntity<ReleaseDTO> updateRelease(@Valid @RequestBody ReleaseDTO releaseDTO) throws URISyntaxException {
        log.debug("REST request to update Release : {}", releaseDTO);
        if (releaseDTO.getId() == null) {
            return createRelease(releaseDTO);
        }
        ReleaseDTO result = releaseService.save(releaseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("release", releaseDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /releases : get all the releases.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of releases in body
     */
    @GetMapping("/releases")
    @Timed
    public List<ReleaseDTO> getAllReleases() {
        log.debug("REST request to get all Releases for the current user");
        return releaseService.findAllForCurrentUser(); //releaseService.findAll();
    }

    /**
     * GET  /projects/{projectId}/releases/default : get the default release for the project
     *
     * @param projectId the id of the project the default release shall be returned
     * @return the ResponseEntity with status 200 (OK) and the default release in the body
     */
    @GetMapping("/projects/{projectId}/releases/default")
    @Timed
    public ResponseEntity<ReleaseDTO> getDefaultRelease(@PathVariable Long projectId) {
        log.debug("REST request to get the default release for project {}", projectId);

        if(containsId(projectService.findAllOfCurrentUser(),projectId)) {
            ReleaseDTO defaultReleaseForProject = releaseService.getDefaultReleaseForProject(projectId);
            return Optional.ofNullable(defaultReleaseForProject)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    private static boolean containsId(List<ProjectDTO> list, long id) {
        for (ProjectDTO proj : list) {
            System.out.println("comparing proj"+ proj.getId() + " with "+ id);
            if (proj.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * GET  /projects/{projectId}/releases : get all releases for the given project
     *
     * @param projectId the id of the project all releases shall be returned
     * @return the ResponseEntity with status 200 (OK) and the list of releases in the body
     */
    @GetMapping("/projects/{projectId}/releases")
    @Timed
    public ResponseEntity<List<ReleaseDTO>> getAllReleasesForProject(@PathVariable Long projectId) {
        log.debug("REST request to get the default release for project {}", projectId);
        if(containsId(projectService.findAllOfCurrentUser(),projectId)) {
            List<ReleaseDTO> releasesForProject = releaseService.findAllForProject(projectId);
            return Optional.ofNullable(releasesForProject)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * GET  /releases/:id : get the "id" release.
     *
     * @param id the id of the releaseDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the releaseDTO, or with status 404 (Not Found)
     */
    @GetMapping("/releases/{id}")
    @Timed
    public ResponseEntity<ReleaseDTO> getRelease(@PathVariable Long id) {
        log.debug("REST request to get Release : {}", id);
        ReleaseDTO releaseDTO = releaseService.findOne(id);
        if(releaseDTO==null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(containsId(projectService.findAllOfCurrentUser(),releaseDTO.getProjectId())) {
            return Optional.ofNullable(releaseDTO)
                .map(result -> new ResponseEntity<>(
                    result,
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    /**
     * GET  /releases/counttranslations/:id : get the number of translations in the "id" release.
     *
     * @param id the id of the releaseDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the Number, or with status 404 (Not Found)
     */
    @GetMapping("/releases/counttranslations/{id}")
    @Timed
    public ResponseEntity<Pair<Integer,Integer>> getCountTranslationsForRelease(@PathVariable Long id) {
        log.debug("REST request to get Count of Translations for Release : {}", id);
        Integer count = releaseService.countTranslations(id);
        Pair<Integer,Integer> pair = Pair.of(count,1); //TODO zweite abfrage für die anzahl der definitionen
        return Optional.ofNullable(pair)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * GET  /releases/project/:id : get the current release for project "id".
     *
     * @param id the id of the Project to retrieve the current release for
     * @return the ResponseEntity with status 200 (OK) and with body the releaseDTO, or with status 404 (Not Found)
     */
//    @GetMapping("/releases/project/{id}")
//    @Timed
//    public ResponseEntity<ReleaseDTO> getCurrentReleaseByProjectId(@PathVariable Long id) {
//        log.debug("Rest Request to get current Release of Project : {}", id);
//        ReleaseDTO releaseDTO = releaseService.findCurrentByProjectId(id);
//        return Optional.ofNullable(releaseDTO)
//            .map(result -> new ResponseEntity<>(
//                result,
//                HttpStatus.OK))
//            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }

    /**
     * DELETE  /releases/:id : delete the "id" release.
     *
     * @param id the id of the releaseDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/releases/{id}")
    @Timed
    public ResponseEntity<Void> deleteRelease(@PathVariable Long id) {
        log.debug("REST request to delete Release : {}", id);
        releaseService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("release", id.toString())).build();
    }

}
