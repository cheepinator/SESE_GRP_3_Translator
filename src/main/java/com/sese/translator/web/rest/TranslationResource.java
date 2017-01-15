package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.sese.translator.domain.Definition;
import com.sese.translator.domain.Project;
import com.sese.translator.domain.Translation;
import com.sese.translator.domain.User;
import com.sese.translator.repository.DefinitionRepository;
import com.sese.translator.repository.ProjectRepository;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.repository.UserRepository;
import com.sese.translator.service.DefinitionService;
import com.sese.translator.service.ProjectService;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.dto.*;
import com.sese.translator.web.rest.util.HeaderUtil;
import com.sese.translator.web.rest.util.PaginationUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * REST controller for managing Translation.
 */
@RestController
@RequestMapping("/api")
public class TranslationResource {

    public static final String DEFAULT_LANGUAGE_CODE_ENGLISH = "en";
    private final Logger log = LoggerFactory.getLogger(TranslationResource.class);

    @Inject
    private TranslationService translationService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ReleaseService releaseService;

    @Inject
    private TranslationRepository translationRepository;

    @Inject
    private DefinitionRepository definitionRepository;

    @Inject
    private DefinitionService definitionService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private ProjectRepository projectRepository;

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
     * POST /release/next_definition get the next 10 open Translation.
     *
     * @param nextTranslationDTO the request object
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping("/release/next_translation")
    @Timed
    public ResponseEntity<List<TranslationDTO>> getNextOpenTranslations(@Valid @RequestBody NextTranslationDTO nextTranslationDTO) {
        log.debug("REST request the next definition of Release : {} with language: {}", nextTranslationDTO.getReleaseId(), nextTranslationDTO.getLanguageId());
        List<TranslationDTO> translationDTOs = translationService.getNextOpenTranslation(nextTranslationDTO);
        if (translationDTOs == null) {
            translationDTOs = new ArrayList<>();
        }
        return new ResponseEntity<>(translationDTOs, HttpStatus.OK);
    }

//    /**
//     * GET sdfjkasf
//     *
//     * @return with status 200 (OK)
//     */
//    @GetMapping("/project/{projectId}/file/{fileSome}")
//    @Timed
//    public ResponseEntity<Void> getNextOpenTranslations(@PathVariable Long projectId, @PathVariable String fileSome) {
//        log.debug("GOT SOME FILE!!!!!!: {}", fileSome);
//
//        return ResponseEntity.ok().build();
//    }

    /**
     * POST
     *
     * @return with status 200 (OK)
     */
    @PostMapping("/projects/{projectId}/fileUpload")
    @Timed
    @Transactional
    public ResponseEntity<Void> uploadTranslations(@PathVariable Long projectId, @RequestParam("file") MultipartFile file,
                                                   @RequestParam("path") String path) {
        ProjectDTO project = projectService.findOne(projectId);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        if (!file.isEmpty()) {
            try {
                CharsetMatch detect = new CharsetDetector().setText(file.getBytes()).detect();
                parseUploadedFile(project, detect.getReader(), file.getOriginalFilename(), path);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok().build();
    }

    private void parseUploadedFile(ProjectDTO project, Reader fileContentAsString, String originalFilename, String path) {
        log.info("Got file upload for project {}: {}, {}", project.getId(), originalFilename, path);
        String languageCode;
        if (path != null && !path.isEmpty()) {
            languageCode = detectLanguageCode(originalFilename, path);
        } else {
            languageCode = FilenameUtils.getBaseName(originalFilename);
        }
        log.info("Detected language code: {}. {}, {}", languageCode, originalFilename, path);
        if (originalFilename.endsWith(".strings")) {
            parseIOSFile(fileContentAsString, languageCode, project);
        } else if (originalFilename.endsWith(".xml")) {
            parseAndroidFile(fileContentAsString, languageCode, project);
        } else if (originalFilename.endsWith(".json")) {
            // todo: not supported
        }
    }

    private String detectLanguageCode(String originalFilename, String path) {
        Path filePath = Paths.get(path);
        for (Path pathPart : filePath) {
            String pathString = pathPart.toString();
            if (originalFilename.endsWith(".strings")) {
                if (pathString.endsWith(".lproj")) {
                    return pathString.substring(0, pathString.indexOf(".lproj"));
                }
            } else if (originalFilename.endsWith(".xml")) {
                if (pathString.startsWith("values-")) {
                    return pathString.substring(pathString.indexOf("-") + 1);
                } else if (pathString.equals("values")) {
                    return DEFAULT_LANGUAGE_CODE_ENGLISH;
                }
            } else if (originalFilename.endsWith(".json")) {
                // todo: not supported
            }
        }
        return filePath.getFileName().toString();
    }

    private void parseIOSFile(Reader fileContentAsString, String languageCode, ProjectDTO project) {
        Scanner scanner = new Scanner(fileContentAsString);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.startsWith("\"") || !line.endsWith(";")) {
                continue;
            }
            String[] parts = stripLineEndDelimiter(line).split("=");
            if (parts.length != 2) {
                continue;
            }
            String definitionCode = stripApostrophes(parts[0].trim());
            String definitionText = stripApostrophes(parts[1].trim());

            if (!definitionCode.isEmpty() && !definitionText.isEmpty()) {
                updateOrCreateDefinition(project, definitionCode, definitionText, languageCode);
            }
        }
    }

    private String stripLineEndDelimiter(String line) {
        return line.substring(0, line.length() -1);
    }

    private String stripApostrophes(String text) {
        return text.substring(1, text.length() - 1);
    }

    private void parseAndroidFile(Reader fileContentAsString, String languageCode, ProjectDTO project) {
        // todo: parse and use method below to write to db
    }

    private void updateOrCreateDefinition(ProjectDTO project, String definitionCode, String definitionText, String languageCode) {
        List<Definition> definitions = definitionRepository.findByProjectIdAndDefinitionCode(project.getId(), definitionCode);
        if (!definitions.isEmpty()) {
            if (languageCode.equals(DEFAULT_LANGUAGE_CODE_ENGLISH)) {
                // update original text
                updateOriginalText(definitions.get(0), definitionText);
            } else {
                updateTranslationIfFound(definitions.get(0), languageCode, definitionText);
            }
        } else if (languageCode.equals(DEFAULT_LANGUAGE_CODE_ENGLISH)) {
            createNewDefinition(project, definitionCode, definitionText);
        }
    }

    private void updateOriginalText(Definition definition, String definitionText) {
        String oldOriginalText = definition.getOriginalText();
        if (!oldOriginalText.equals(definitionText)) {
            definition.setOriginalText(definitionText);
            definitionRepository.save(definition);
            translationService.markAllTranslationsForDefinitionAsUpdateNeeded(definition.getId());
            log.debug("Updated the original text of definition {}", definition.getId());
        }
    }

    private void updateTranslationIfFound(Definition definition, String languageCode, String definitionText) {
        Translation translation = translationRepository.findByDefinitionIdAndLanguageCode(definition.getId(), languageCode);
        if (translation != null) {
            String oldTranslatedText = translation.getTranslatedText();
            if (oldTranslatedText == null || !oldTranslatedText.equals(definitionText)) {
                translation.setTranslatedText(definitionText);
                translation.setUpdateNeeded(false);
                translationRepository.save(translation);
                log.debug("Updated translation for definition {} and language {}", definition.getId(), languageCode);
            }
        }
    }

    private void createNewDefinition(ProjectDTO project, String definitionCode, String definitionText) {
        DefinitionDTO definitionDTO = new DefinitionDTO();
        definitionDTO.setCode(definitionCode);
        definitionDTO.setOriginalText(definitionText);
        definitionDTO.setReleaseId(releaseService.getDefaultReleaseForProject(project.getId()).getId());
        DefinitionDTO newDefinition = definitionService.save(definitionDTO);
        log.debug("Created new definition from import for project {}: {}", project.getId(), newDefinition);
    }

    /**
     * GET  /projects/{projectId}/release/{versionTag}/language/{languageCode} : get all translations from a specific version and language for the project
     *
     * @param projectId    the id of the project
     * @param versionTag    the versionTag of the project
     * @param languageCode    the languageCode of the project
     * @return the ResponseEntity with status 200 (OK) and with body the definitionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/projects/{projectId}/release/{versionTag}/language/{languageCode}/user/{username}/pass/{password}")
    @ResponseBody
    @Transactional
    public ResponseEntity exportApi(@PathVariable Long projectId, @PathVariable String versionTag, @PathVariable String languageCode,
                                    @PathVariable String username, @PathVariable String password) {

        Optional<User> sdfsdf = userRepository.findOneByLogin(username);
        if(sdfsdf.isPresent()) {
            if(!passwordEncoder.matches(password, sdfsdf.get().getPassword())) {
                return new ResponseEntity<>("username or password not correct!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("username or password not correct!", HttpStatus.BAD_REQUEST);
        }

        List<Project> allOfCurrentUser = projectRepository.findByOwnerIsUser(username);
        boolean usersProject = false;
        for (Project project : allOfCurrentUser) {
            if (project.getId().equals(projectId)) {
                usersProject = true;
            }
        }
        if (!usersProject) {
            return new ResponseEntity<>("logged in user is not associated with the project!", HttpStatus.BAD_REQUEST);
        }

        List<Translation> translationList = translationRepository.findByProjectIdLanguageIdReleaseId(projectId,
            versionTag, languageCode);

        StringBuilder stringBuilder = new StringBuilder();
        Boolean first = true;
        stringBuilder.append("[");
        for (Translation t : translationList) {
            if(first) {
                first = false;
            } else {
                stringBuilder.append(",");
            }
            stringBuilder.append("{ \"translatedText\" : \"");
            stringBuilder.append(t.getTranslatedText().replace("\"", "\\\""));
            stringBuilder.append("\", \"code\" : \"");
            stringBuilder.append(t.getDefinition().getCode().replace("\"", "\\\""));
            stringBuilder.append("\", \"originalText\" : \"");
            stringBuilder.append(t.getDefinition().getOriginalText().replace("\"", "\\\""));
            stringBuilder.append("\" }");
        }
        stringBuilder.append("]");

        return new ResponseEntity<>(stringBuilder.toString(), HttpStatus.OK);
    }

    /**
     * GET  /projects/{projectId}/export/{ex} : download all translations for the project
     *
     * @param projectId    the id of the project
     * @return the ResponseEntity with status 200 (OK) and with body the definitionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/projects/{projectId}/export/{ex}")
    @ResponseBody
    @Transactional
    public ResponseEntity downloadTranslations(@PathVariable Long projectId, @PathVariable String ex) {
        log.debug("REST request to get all Translations for project with id: {}", projectId);
        //todo: also some security that only a developer of a project can do this

        ProjectDTO project = projectService.findOne(projectId);
        List<ReleaseDTO> releaseList = releaseService.findAllForProject(projectId);

        StringBuilder stringBuilder = new StringBuilder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            for(ReleaseDTO releaseDTO : releaseList) {
                for(LanguageDTO languageDTO : project.getLanguages()) {
                    List<Translation> translationList = translationRepository.findByProjectIdLanguageIdReleaseId(projectId,
                        releaseDTO.getVersionTag(), languageDTO.getCode());
                    appendTranslation(stringBuilder, translationList, ex);
                    zipOutputStream.putNextEntry(new ZipEntry(releaseDTO.getVersionTag() + "/" + languageDTO.getCode() + getFileEnding(ex)));
                    zipOutputStream.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
                    stringBuilder.setLength(0);
                }
            List<Definition> getDefinitionsFromRelease = definitionRepository.findByProjectIdAndVersionTag(projectId, releaseDTO.getVersionTag());
            appendDefinition(stringBuilder, getDefinitionsFromRelease, ex);
            zipOutputStream.putNextEntry(new ZipEntry(releaseDTO.getVersionTag() + "/" + DEFAULT_LANGUAGE_CODE_ENGLISH + getFileEnding(ex)));
            zipOutputStream.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            stringBuilder.setLength(0);
            }
        } catch (IOException e) {
            log.error("Failed to generate zip file to download", e);
            return null;
        }
        byte[] downloadFile = outputStream.toByteArray();

        // set the headers
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "zip"));
        header.set("Content-Disposition", "inline; filename=" + projectId + ".zip");
        header.setContentLength(downloadFile.length);

        return new ResponseEntity<>(downloadFile, header, HttpStatus.OK);
    }

    private String getFileEnding(String ex) {
        switch (ex) {
            case "ios":
                return ".strings";
            case "android":
                return ".xml";
            case "web":
                return ".json";
        }
        return "";
    }

    private void appendTranslation(StringBuilder stringBuilder, List<Translation> translationList, String export) {
        if(translationList.size() == 0) {
            return;
        }
        switch (export) {
            case "ios":
                for (Translation t : translationList) {
                    buildIosString(stringBuilder, t.getDefinition().getCode(), t.getTranslatedText());
                }
                break;
            case "web":
                buildWebStringHeader(stringBuilder, translationList.get(0).getLanguage().getCode());
                Boolean first = true;
                for (Translation t : translationList) {
                    if(first) {
                        buildWebString(stringBuilder, t.getDefinition().getCode(), t.getTranslatedText(), true);
                        first = false;
                    } else {
                        buildWebString(stringBuilder, t.getDefinition().getCode(), t.getTranslatedText(), false);
                    }
                }
                buildWebStringFooter(stringBuilder);
                break;
            case "android":
                buildAndroidHeader(stringBuilder);
                for (Translation t : translationList) {
                    buildAndroid(stringBuilder, t.getDefinition().getCode(), t.getTranslatedText());
                }
                buildAndroidFooter(stringBuilder);
                break;
        }
    }

    private void appendDefinition(StringBuilder stringBuilder, List<Definition> definitionList, String export) {
        if(definitionList.size() == 0) {
            return;
        }
        switch (export) {
            case "ios":
                for (Definition t : definitionList) {
                    buildIosString(stringBuilder, t.getCode(), t.getOriginalText());
                }
                break;
            case "web":
                buildWebStringHeader(stringBuilder, DEFAULT_LANGUAGE_CODE_ENGLISH);
                Boolean first = true;
                for (Definition t : definitionList) {
                    if(first) {
                        buildWebString(stringBuilder, t.getCode(), t.getOriginalText(), true);
                        first = false;
                    } else {
                        buildWebString(stringBuilder, t.getCode(), t.getOriginalText(), false);
                    }
                }
                buildWebStringFooter(stringBuilder);
                break;
            case "android":
                buildAndroidHeader(stringBuilder);
                for (Definition t : definitionList) {
                    buildAndroid(stringBuilder, t.getCode(), t.getOriginalText());
                }
                buildAndroidFooter(stringBuilder);
                break;
        }
    }

    private void buildIosString(StringBuilder stringBuilder, String definitionCode, String translatedText) {
        definitionCode = definitionCode.replace("\"", "\\\"");
        translatedText = translatedText.replace("\"", "\\\"");
        stringBuilder.append("\"").append(definitionCode).append("\" = \"").append(translatedText).append("\";\n");
    }

    private void buildWebString(StringBuilder stringBuilder, String definitionCode, String translatedText, Boolean first) {
        definitionCode = definitionCode.replace("\"", "\\\"");
        translatedText = translatedText.replace("\"", "\\\"");
        if (!first) {
            stringBuilder.append(",\n");
        }
        stringBuilder.append("\"").append(definitionCode).append("\":{\"").append(translatedText).append("\" : \"").append(translatedText).append("\"}");
    }

    private void buildWebStringHeader(StringBuilder stringBuilder, String languageCode) {
        languageCode = languageCode.replace("\"", "\\\"");
        stringBuilder.append("{\n\"").append(languageCode).append("\":\n{\n");
    }

    private void buildWebStringFooter(StringBuilder stringBuilder) {
        stringBuilder.append("\n}\n}");
    }

    private void buildAndroidHeader(StringBuilder stringBuilder) {
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ressources>\n");
    }

    private void buildAndroidFooter(StringBuilder stringBuilder) {
        stringBuilder.append("</ressources>");
    }

    private void buildAndroid(StringBuilder stringBuilder, String definitionCode, String translatedText) {
        definitionCode = definitionCode.replace("\"", "\\\"");
        translatedText = translatedText.replace("\"", "\\\"");
        stringBuilder.append("<string name=\"").append(definitionCode).append("\">").append(translatedText).append("</string>\n");
    }

}
