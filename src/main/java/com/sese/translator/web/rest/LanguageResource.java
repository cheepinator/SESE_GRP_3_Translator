package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sese.translator.service.LanguageService;
import com.sese.translator.service.ProjectService;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.dto.LanguageDTO;
import com.sese.translator.service.dto.ProgressDTO;
import com.sese.translator.service.dto.ProjectDTO;
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
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Language.
 */
@RestController
@RequestMapping("/api")
public class LanguageResource {

    private final Logger log = LoggerFactory.getLogger(LanguageResource.class);

    @Inject
    private LanguageService languageService;

    @Inject
    private ProjectService projectService;

    @Inject
    private TranslationService translationService;

    /**
     * POST  /languages : Create a new language.
     *
     * @param languageDTO the languageDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new languageDTO, or with status 400 (Bad Request) if the language has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/languages")
    @Timed
    public ResponseEntity<LanguageDTO> createLanguage(@Valid @RequestBody LanguageDTO languageDTO) throws URISyntaxException {
        log.debug("REST request to save Language : {}", languageDTO);
        if (languageDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("language", "idexists", "A new language cannot already have an ID")).body(null);
        }
        LanguageDTO result = languageService.save(languageDTO);
        return ResponseEntity.created(new URI("/api/languages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("language", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /languages : Updates an existing language.
     *
     * @param languageDTO the languageDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated languageDTO,
     * or with status 400 (Bad Request) if the languageDTO is not valid,
     * or with status 500 (Internal Server Error) if the languageDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/languages")
    @Timed
    public ResponseEntity<LanguageDTO> updateLanguage(@Valid @RequestBody LanguageDTO languageDTO) throws URISyntaxException {
        log.debug("REST request to update Language : {}", languageDTO);
        if (languageDTO.getId() == null) {
            return createLanguage(languageDTO);
        }
        LanguageDTO result = languageService.save(languageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("language", languageDTO.getId().toString()))
            .body(result);
    }

    /**
     * POST  /projects/{projectId}/languages : Create a new language for a project
     *
     * @param languageDTO the languageDTO to create and add to the given project id
     * @return the ResponseEntity with status 201 (Created) and with body the new languageDTO,
     * or with status 400 (Bad Request) if the language has already an ID
     * or with status 404 (Not Found) if the project with the given ID does not exist
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/projects/{projectId}/languages")
    @Timed
    public ResponseEntity<?> createLanguageForProject(@Valid @RequestBody LanguageDTO languageDTO,
                                                      @PathVariable Long projectId) throws URISyntaxException {
        log.debug("REST request to save Language for project with id {} : {}", projectId, languageDTO);
        if (languageDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("language", "idexists", "A new language cannot already have an ID")).body(null);
        }
        ProjectDTO projectDTO = projectService.findOne(projectId);
        if (projectDTO == null) {
            return ResponseEntity.notFound().build();
        }
        if (languageService.languageCodeAlreadyExistsForProject(projectId, languageDTO.getCode())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("language", "codeexists",
                "A language with the given code already exists for the project")).body(null);
        }
        languageDTO = languageService.save(languageDTO);
        projectService.addLanguageToProject(projectDTO, languageDTO);
        translationService.addMissingTranslationsForProject(projectDTO);

        return ResponseEntity.created(new URI("/api/languages/" + languageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("language", languageDTO.getId().toString()))
            .body(languageDTO);
    }

    /**
     * DELETE  /projects/{projectId}/languages : Remove a language from a project. This also removes all corresponding translations in that language
     *
     * @return
     */
    @DeleteMapping("/projects/{projectId}/languages/{languageId}")
    @Timed
    public ResponseEntity<?> removeLanguageFromProject(@PathVariable Long projectId, @PathVariable Long languageId) {
        log.debug("REST request to remove Language with id {} for project with id {} : {}", languageId, projectId);
        ProjectDTO projectDTO = projectService.findOne(projectId);
        if (projectDTO == null) {
            return ResponseEntity.notFound().build();
        }
        LanguageDTO languageDTO = languageService.findOne(languageId);
        if (languageDTO == null) {
            return ResponseEntity.notFound().build();
        }
        LanguageDTO updatedLanguage = projectService.removeLanguageFromProject(projectDTO, languageDTO);
        translationService.removeAllTranslationsForProjectAndLanguage(projectDTO, languageDTO);
        languageService.delete(languageId);

        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("language", languageId.toString())).build();
    }

    /**
     * GET  /projects/{projectId}/languages : Get all Languages associated with a project
     *
     * @return the ResponseEntity with status 201 (Created) and with body the new languageDTO,
     * or with status 400 (Bad Request) if the language has already an ID
     * or with status 404 (Not Found) if the project with the given ID does not exist
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @GetMapping("/projects/{projectId}/languages")
    @Timed
    public ResponseEntity<?> createLanguageForProject(@PathVariable Long projectId) throws URISyntaxException {
        log.debug("REST request to get Languages for project with id {}", projectId);
        List<LanguageDTO> languages = languageService.findByProjectId(projectId);

        return new ResponseEntity<>(languages, HttpStatus.OK);
    }

    /**
     * GET  /projects/{projectId}/languages-progress/ : get the progress of the given project
     *
     * @param projectId the id of project we are interested in the progress
     * @return the ResponseEntity with status 200 (OK) and with body the list of progress objects, or with status 404 (Not Found)
     */
    @GetMapping("/projects/{projectId}/languages-progress")
    @Timed
    public ResponseEntity<?> getLanguageProgress(@PathVariable Long projectId) {
        log.debug("REST request to get the progress of all languages in the project with id {}", projectId);
        ProjectDTO projectDTO = projectService.findOne(projectId);
        if (projectDTO == null) {
            return ResponseEntity.notFound().build();
        }

        List<ProgressDTO> progressForProject = translationService.getProgressForProject(projectDTO);
        return ResponseEntity.ok(progressForProject);
    }

    /**
     * GET  /languages : get all the languages.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of languages in body
     */
    @GetMapping("/languages")
    @Timed
    public List<LanguageDTO> getAllLanguages() {
        log.debug("REST request to get all Languages");
        return languageService.findAll();
    }

    /**
     * GET  /languages/:id : get the "id" language.
     *
     * @param id the id of the languageDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the languageDTO, or with status 404 (Not Found)
     */
    @GetMapping("/languages/{id}")
    @Timed
    public ResponseEntity<LanguageDTO> getLanguage(@PathVariable Long id) {
        log.debug("REST request to get Language : {}", id);
        LanguageDTO languageDTO = languageService.findOne(id);
        return Optional.ofNullable(languageDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /languages/:id : delete the "id" language.
     *
     * @param id the id of the languageDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/languages/{id}")
    @Timed
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) {
        log.debug("REST request to delete Language : {}", id);
        languageService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("language", id.toString())).build();
    }

}
