package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sese.translator.domain.Definition;
import com.sese.translator.domain.Language;
import com.sese.translator.domain.Release;
import com.sese.translator.domain.Translation;
import com.sese.translator.repository.DefinitionRepository;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.dto.NextTranslationDTO;
import com.sese.translator.service.dto.TranslationDTO;
import com.sese.translator.web.rest.util.HeaderUtil;
import com.sese.translator.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * REST controller for managing Translation.
 */
@RestController
@RequestMapping("/api")
public class TranslationResource {

    private final Logger log = LoggerFactory.getLogger(TranslationResource.class);

    @Inject
    private TranslationService translationService;

    @Inject
    private TranslationRepository translationRepository;

    @Inject
    private DefinitionRepository definitionRepository;

    /**
     * POST  /translations : Create a new translation.
     *
     * @param translationDTO the translationDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new translationDTO, or with status 400 (Bad Request) if the translation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/translations")
    @Timed
    public ResponseEntity<TranslationDTO> createTranslation(@Valid @RequestBody TranslationDTO translationDTO) throws URISyntaxException {
        log.debug("REST request to save Translation : {}", translationDTO);
        if (translationDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("translation", "idexists", "A new translation cannot already have an ID")).body(null);
        }
        TranslationDTO result = translationService.save(translationDTO);
        return ResponseEntity.created(new URI("/api/translations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("translation", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /translations : Updates an existing translation.
     *
     * @param translationDTO the translationDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated translationDTO,
     * or with status 400 (Bad Request) if the translationDTO is not valid,
     * or with status 500 (Internal Server Error) if the translationDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/translations")
    @Timed
    public ResponseEntity<TranslationDTO> updateTranslation(@Valid @RequestBody TranslationDTO translationDTO) throws URISyntaxException {
        log.debug("REST request to update Translation : {}", translationDTO);
        if (translationDTO.getId() == null) {
            return createTranslation(translationDTO);
        }
        TranslationDTO result = translationService.update(translationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("translation", translationDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /translations : get all the translations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of translations in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/translations")
    @Timed
    public ResponseEntity<List<TranslationDTO>> getAllTranslations(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Translations");
        Page<TranslationDTO> page = translationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/translations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /translations/:id : get the "id" translation.
     *
     * @param id the id of the translationDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the translationDTO, or with status 404 (Not Found)
     */
    @GetMapping("/translations/{id}")
    @Timed
    public ResponseEntity<TranslationDTO> getTranslation(@PathVariable Long id) {
        log.debug("REST request to get Translation : {}", id);
        TranslationDTO translationDTO = translationService.findOne(id);
        return Optional.ofNullable(translationDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /definitions/:id : get the "id" definition.
     *
     * @param id the id of the definitionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the definitionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/definitions/{id}/translations")
    @Timed
    public ResponseEntity<List<TranslationDTO>> getTranslationsOfDefinition(@PathVariable Long id) {
        log.debug("REST request to get Translations for definition with id: {}", id);
        List<TranslationDTO> forDefinition = translationService.findForDefinition(id);
        return new ResponseEntity<>(forDefinition, HttpStatus.OK);
    }

    /**
     * GET  /projects/{projectId}/translations : get all translations for the project
     *
     * @param projectId the id of the project to get all translations for to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the definitionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/projects/{projectId}/translations")
    @Timed
    public ResponseEntity<List<TranslationDTO>> getAllTranslationsForProject(@PathVariable Long projectId) {
        log.debug("REST request to get all Translations for project with id: {}", projectId);
        List<TranslationDTO> forDefinition = translationService.findForProject(projectId);
        return new ResponseEntity<>(forDefinition, HttpStatus.OK);
    }

    /**
     * DELETE  /translations/:id : delete the "id" translation.
     *
     * @param id the id of the translationDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/translations/{id}")
    @Timed
    public ResponseEntity<Void> deleteTranslation(@PathVariable Long id) {
        log.debug("REST request to delete Translation : {}", id);
        translationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("translation", id.toString())).build();
    }

    /**
     * POST /release/next_definition get the next open Translation.
     *
     * @param nextTranslationDTO the request object
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping("/release/next_translation")
    @Timed
    public ResponseEntity<TranslationDTO> getNextOpenTranslation(@Valid @RequestBody NextTranslationDTO nextTranslationDTO) {
        log.debug("REST request the next definition of Release : {} with language: {}", nextTranslationDTO.getReleaseId(), nextTranslationDTO.getLanguageId());
        TranslationDTO translationDTO = translationService.getNextOpenTranslation(nextTranslationDTO);
        if (translationDTO == null) {
            translationDTO = new TranslationDTO();
        }
        return new ResponseEntity<>(translationDTO, HttpStatus.OK);
    }

    /**
     * GET  /projects/{projectId}/release/{versionTag}/language/{languageCode} : get all translations for the project
     *
     * @param projectId the id of the project
     * @param versionTag the version of the translation
     * @param languageCode the language of the translation
     * @return the ResponseEntity with status 200 (OK) and with body the definitionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/projects/{projectId}/release/{versionTag}/language/{languageCode}")
    @ResponseBody
    public ResponseEntity downloadTranslations(@PathVariable Long projectId, @PathVariable String versionTag, @PathVariable String languageCode) {
        log.debug("REST request to get all Translations for project with id: {} and release {} and language {}", projectId, versionTag, languageCode);
        //todo: remove the default tags and language if implemented
        //todo: also some security that only a developer of a project can do this

        List<Translation> byProjectIdLanguageIdReleaseId = translationRepository.findByProjectIdLanguageIdReleaseId(projectId, Release.DEFAULT_TAG, Language.DEFAULT_LANGUAGE);

        StringBuilder stringBuilder = new StringBuilder();
        for (Translation t : byProjectIdLanguageIdReleaseId) {
            stringBuilder.append("'" + t.getDefinition().getCode() + "' = '" + t.getTranslatedText() + "';\n");
        }
        byte[] language_file = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);

        // append the english definitions
        stringBuilder.setLength(0);
        List<Definition> byProjectIdAndVersionTag = definitionRepository.findByProjectIdAndVersionTag(projectId, Release.DEFAULT_TAG);
        for (Definition d : byProjectIdAndVersionTag) {
            stringBuilder.append("'" + d.getCode() + "' = '" + d.getOriginalText() + "';\n");
        }
        byte[] definition_file = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);

        try {
            // make the zip file...
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("tmp.zip"), StandardCharsets.UTF_8);
            zipOutputStream.putNextEntry(new ZipEntry("german.strings"));
            zipOutputStream.write(language_file);
            zipOutputStream.putNextEntry(new ZipEntry("english.strings"));
            zipOutputStream.write(definition_file);
            zipOutputStream.close();

            File zipfile = new File("tmp.zip");
            byte[] downloadFile = Files.readAllBytes(zipfile.toPath());

            // set the headers
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "zip"));
            header.set("Content-Disposition", "inline; filename=" + projectId + ".zip");
            header.setContentLength(downloadFile.length);

            return new ResponseEntity<>(downloadFile, header, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
