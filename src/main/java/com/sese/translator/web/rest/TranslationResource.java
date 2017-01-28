package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.neovisionaries.i18n.LocaleCode;
import com.sese.translator.domain.*;
import com.sese.translator.domain.enumeration.Projectrole;
import com.sese.translator.repository.*;
import com.sese.translator.service.*;
import com.sese.translator.service.dto.*;
import com.sese.translator.web.rest.parsing.ElementHandler;
import com.sese.translator.web.rest.parsing.UploadedFile;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
    private ProjectService projectService;

    @Inject
    private UserService userService;

    @Inject
    private ReleaseService releaseService;

    @Inject
    private TranslationRepository translationRepository;

    @Inject
    private DefinitionRepository definitionRepository;

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private LanguageService languageService;

    @Inject
    private DefinitionService definitionService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ProjectassignmentRepository projectassignmentRepository;

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

    /**
     * POST
     *
     * @return with status 200 (OK)
     */
    @PostMapping("/projects/{projectId}/fileUpload")
    @Timed
    @Transactional
    public ResponseEntity<Void> uploadTranslations(@PathVariable Long projectId, HttpServletRequest request) {
        ProjectDTO project = projectService.findOne(projectId);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        if (!(request instanceof MultipartRequest)) {
            return ResponseEntity.badRequest().build();
        }
        boolean translatorOnly = isTranslatorOnly(projectId);
        ArrayList<UploadedFile> uploadedFiles = getSortedUploadedFiles(request);
        for (UploadedFile uploadedFile : uploadedFiles) {
            if (translatorOnly && uploadedFile.isEnglishLanguageCode()) {
                log.debug("Skip uploaded definition file as user is translator only and file contains new definitions: {}", uploadedFile);
                continue;
            }
            try {
                parseUploadedFile(project, uploadedFile);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok().build();
    }

    private boolean isTranslatorOnly(Long projectId) {
        List<Projectassignment> projectAssignments = projectassignmentRepository.findByAssignedUserAndAssignedProjectId(userService.getUserWithAuthorities(), projectId);
        return getRoles(projectAssignments).anyMatch(Predicate.isEqual(Projectrole.TRANSLATOR))
            && getRoles(projectAssignments).noneMatch(Predicate.isEqual(Projectrole.DEVELOPER));
    }

    private Stream<Projectrole> getRoles(List<Projectassignment> projectAssignments) {
        return projectAssignments.stream().map(Projectassignment::getRole).distinct();
    }

    private ArrayList<UploadedFile> getSortedUploadedFiles(HttpServletRequest request) {
        int counter = 0;
        ArrayList<UploadedFile> uploadedFiles = new ArrayList<>();
        for (MultipartFile multipartFile : ((MultipartRequest) request).getFileMap().values()) {
            if (!multipartFile.isEmpty()) {
                uploadedFiles.add(new UploadedFile(multipartFile, request.getParameter("paths[" + counter + "]")));
            }
            counter++;
        }
        uploadedFiles.sort(Comparator.comparing(UploadedFile::isEnglishLanguageCode).reversed());
        return uploadedFiles;
    }

    private void parseUploadedFile(ProjectDTO project, UploadedFile uploadedFile) throws IOException, SAXException, ParserConfigurationException {
        log.info("Got file upload for project {}: {}", project.getId(), uploadedFile);
        if (uploadedFile.isIOSFile()) {
            parseIOSFile(uploadedFile, project);
        } else if (uploadedFile.isAndroidFile()) {
            parseAndroidFile(uploadedFile, project);
        }
    }

    private void parseIOSFile(UploadedFile uploadedFile, ProjectDTO project) throws IOException {
        Scanner scanner = new Scanner(uploadedFile.getReader());
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
                updateOrCreateDefinition(project, definitionCode, definitionText, uploadedFile);
            }
        }
    }

    private String stripLineEndDelimiter(String line) {
        return line.substring(0, line.length() - 1);
    }

    private String stripApostrophes(String text) {
        return text.substring(1, text.length() - 1);
    }

    private void parseAndroidFile(UploadedFile uploadedFile, ProjectDTO project) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser saxParser = factory.newSAXParser();
        ElementHandler elementHandler = new ElementHandler();
        saxParser.parse(new InputSource(uploadedFile.getReader()), elementHandler);
        elementHandler.getDefinitions().forEach(definitionExtraction -> updateOrCreateDefinition(project, definitionExtraction.getCode(),
            definitionExtraction.getText(), uploadedFile));
    }

    private void updateOrCreateDefinition(ProjectDTO project, String definitionCode, String definitionText, UploadedFile uploadedFile) {
        List<Definition> definitions = definitionRepository.findByProjectIdAndDefinitionCode(project.getId(), definitionCode);
        if (!definitions.isEmpty()) {
            if (uploadedFile.isEnglishLanguageCode()) {
                updateOriginalText(definitions.get(0), definitionText);
            } else {
                updateOrCreateTranslation(project, definitions.get(0), uploadedFile.getLanguageCode(), definitionText);
            }
        } else if (uploadedFile.isEnglishLanguageCode()) {
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

    private void updateOrCreateTranslation(ProjectDTO project, Definition definition, String languageCode, String definitionText) {
        createLanguageIfNotFound(project, languageCode);
        Translation translation = translationRepository.findByDefinitionIdAndLanguageCode(definition.getId(), languageCode);
        if (translation != null) {
            String oldTranslatedText = translation.getTranslatedText();
            if (oldTranslatedText == null || !oldTranslatedText.equals(definitionText) || translation.isUpdateNeeded()) {
                translation.setTranslatedText(definitionText);
                translation.setUpdateNeeded(false);
                translationRepository.save(translation);
                log.debug("Updated translation for definition {} and language {}", definition.getId(), languageCode);
            }
        }
    }

    private void createLanguageIfNotFound(ProjectDTO project, String languageCode) {
        if (isValidLanguageCode(languageCode) && !languageService.languageCodeAlreadyExistsForProject(project.getId(), languageCode)) {
            LanguageDTO languageDTO = new LanguageDTO();
            languageDTO.setCode(languageCode);
            languageDTO = languageService.save(languageDTO);
            projectService.addLanguageToProject(project, languageDTO);
            translationService.addMissingTranslationsForProject(project);
        }
    }

    private boolean isValidLanguageCode(String languageCode) {
        return LocaleCode.getByCodeIgnoreCase(languageCode) != null;
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
            zipOutputStream.putNextEntry(new ZipEntry(releaseDTO.getVersionTag() + "/" + Language.DEFAULT_LANGUAGE_CODE_ENGLISH + getFileEnding(ex)));
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
                buildWebStringHeader(stringBuilder, Language.DEFAULT_LANGUAGE_CODE_ENGLISH);
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
