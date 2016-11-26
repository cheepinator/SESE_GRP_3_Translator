package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.web.rest.util.HeaderUtil;
import com.sese.translator.service.dto.ReleaseDTO;
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
 * REST controller for managing Release.
 */
@RestController
@RequestMapping("/api")
public class ReleaseResource {

    private final Logger log = LoggerFactory.getLogger(ReleaseResource.class);
        
    @Inject
    private ReleaseService releaseService;

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
        log.debug("REST request to get all Releases");
        return releaseService.findAll();
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
        return Optional.ofNullable(releaseDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

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