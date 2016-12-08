package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sese.translator.service.DefinitionService;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.dto.DefinitionDTO;
import com.sese.translator.web.rest.util.HeaderUtil;
import com.sese.translator.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
 * REST controller for managing Definition.
 */
@RestController
@RequestMapping("/api")
public class DefinitionResource {

    private final Logger log = LoggerFactory.getLogger(DefinitionResource.class);

    @Inject
    private DefinitionService definitionService;

    @Inject
    private TranslationService translationService;

    /**
     * POST  /definitions : Create a new definition.
     *
     * @param definitionDTO the definitionDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new definitionDTO, or with status 400 (Bad Request) if the definition has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/definitions")
    @Timed
    public ResponseEntity<DefinitionDTO> createDefinition(@Valid @RequestBody DefinitionDTO definitionDTO) throws URISyntaxException {
        log.debug("REST request to save Definition : {}", definitionDTO);
        if (definitionDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("definition", "idexists", "A new definition cannot already have an ID")).body(null);
        }
        DefinitionDTO result = definitionService.save(definitionDTO);
        return ResponseEntity.created(new URI("/api/definitions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("definition", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /definitions : Updates an existing definition.
     *
     * @param definitionDTO the definitionDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated definitionDTO,
     * or with status 400 (Bad Request) if the definitionDTO is not valid,
     * or with status 500 (Internal Server Error) if the definitionDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/definitions")
    @Timed
    public ResponseEntity<DefinitionDTO> updateDefinition(@Valid @RequestBody DefinitionDTO definitionDTO) throws URISyntaxException {
        log.debug("REST request to update Definition : {}", definitionDTO);
        if (definitionDTO.getId() == null) {
            return createDefinition(definitionDTO);
        }
        DefinitionDTO previousDefinition = definitionService.findOne(definitionDTO.getId());
        DefinitionDTO result = definitionService.save(definitionDTO);
        if (!previousDefinition.getOriginalText().equals(result.getOriginalText())) {
            log.debug("Original text for updated definition {} has changed, mark all translations as 'update needed'", definitionDTO.getId());
            translationService.markAllTranslationsForDefinitionAsUpdateNeeded(result.getId());
        }
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("definition", definitionDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /definitions : get all the definitions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of definitions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/definitions")
    @Timed
    public ResponseEntity<List<DefinitionDTO>> getAllDefinitions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Definitions");
        Page<DefinitionDTO> page = definitionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/definitions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project/{projectId}/definitions : get all the definitions for a project
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of definitions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/projects/{projectId}/definitions")
    @Timed
    public ResponseEntity<List<DefinitionDTO>> getAllDefinitionsForProject(@PathVariable Long projectId, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Definitions for project {}", projectId);
        Page<DefinitionDTO> page = definitionService.findAllForProject(projectId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/projects/" + projectId + "/definitions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /definitions/:id : get the "id" definition.
     *
     * @param id the id of the definitionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the definitionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/definitions/{id}")
    @Timed
    public ResponseEntity<DefinitionDTO> getDefinition(@PathVariable Long id) {
        log.debug("REST request to get Definition : {}", id);
        DefinitionDTO definitionDTO = definitionService.findOne(id);
        return Optional.ofNullable(definitionDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /definitions/:id : delete the "id" definition.
     *
     * @param id the id of the definitionDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/definitions/{id}")
    @Timed
    public ResponseEntity<Void> deleteDefinition(@PathVariable Long id) {
        log.debug("REST request to delete Definition : {}", id);
        definitionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("definition", id.toString())).build();
    }



}
