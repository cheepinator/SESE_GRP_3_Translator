package com.sese.translator.web.rest;

import com.sese.translator.SeseTranslatorApp;
import com.sese.translator.domain.Definition;
import com.sese.translator.domain.Project;
import com.sese.translator.domain.Release;
import com.sese.translator.domain.Translation;
import com.sese.translator.repository.DefinitionRepository;
import com.sese.translator.repository.ProjectRepository;
import com.sese.translator.repository.ReleaseRepository;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.service.DefinitionService;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.dto.DefinitionDTO;
import com.sese.translator.service.mapper.DefinitionMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DefinitionResource REST controller.
 *
 * @see DefinitionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeseTranslatorApp.class)
public class DefinitionResourceIntTest {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_TEXT = "BBBBBBBBBB";

    @Inject
    private DefinitionRepository definitionRepository;

    @Inject
    private TranslationRepository translationRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private DefinitionMapper definitionMapper;

    @Inject
    private DefinitionService definitionService;

    @Inject
    private TranslationService translationService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restDefinitionMockMvc;

    private Definition definition;
    private Translation translation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DefinitionResource definitionResource = new DefinitionResource();
        ReflectionTestUtils.setField(definitionResource, "definitionService", definitionService);
        ReflectionTestUtils.setField(definitionResource, "translationService", translationService);
        this.restDefinitionMockMvc = MockMvcBuilders.standaloneSetup(definitionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Definition createEntity(EntityManager em) {
        Definition definition = new Definition()
                .code(DEFAULT_CODE)
                .originalText(DEFAULT_ORIGINAL_TEXT);
        return definition;
    }

    @Before
    public void initTest() {
        definition = createEntity(em);
        translation = new Translation().translatedText("Translation");
    }

    @Test
    @Transactional
    public void createDefinition() throws Exception {
        int databaseSizeBeforeCreate = definitionRepository.findAll().size();

        // Create the Definition
        DefinitionDTO definitionDTO = definitionMapper.definitionToDefinitionDTO(definition);

        restDefinitionMockMvc.perform(post("/api/definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(definitionDTO)))
                .andExpect(status().isCreated());

        // Validate the Definition in the database
        List<Definition> definitions = definitionRepository.findAll();
        assertThat(definitions).hasSize(databaseSizeBeforeCreate + 1);
        Definition testDefinition = definitions.get(definitions.size() - 1);
        assertThat(testDefinition.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testDefinition.getOriginalText()).isEqualTo(DEFAULT_ORIGINAL_TEXT);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = definitionRepository.findAll().size();
        // set the field null
        definition.setCode(null);

        // Create the Definition, which fails.
        DefinitionDTO definitionDTO = definitionMapper.definitionToDefinitionDTO(definition);

        restDefinitionMockMvc.perform(post("/api/definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(definitionDTO)))
                .andExpect(status().isBadRequest());

        List<Definition> definitions = definitionRepository.findAll();
        assertThat(definitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOriginalTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = definitionRepository.findAll().size();
        // set the field null
        definition.setOriginalText(null);

        // Create the Definition, which fails.
        DefinitionDTO definitionDTO = definitionMapper.definitionToDefinitionDTO(definition);

        restDefinitionMockMvc.perform(post("/api/definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(definitionDTO)))
                .andExpect(status().isBadRequest());

        List<Definition> definitions = definitionRepository.findAll();
        assertThat(definitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDefinitions() throws Exception {
        // Initialize the database
        definitionRepository.saveAndFlush(definition);

        // Get all the definitions
        restDefinitionMockMvc.perform(get("/api/definitions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(definition.getId().intValue())))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
                .andExpect(jsonPath("$.[*].originalText").value(hasItem(DEFAULT_ORIGINAL_TEXT.toString())));
    }

    @Test
    @Transactional
    public void getAllDefinitions_forAProject_noProjectAvailable() throws Exception {
        // Initialize the database
        definitionRepository.saveAndFlush(definition);

        // Get all the definitions
        restDefinitionMockMvc.perform(get("/api//projects/{projectId}/definitions?sort=id,desc", 1))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @Test
    @Transactional
    public void getAllDefinitions_forAProject() throws Exception {
        // Initialize the database
        definitionRepository.saveAndFlush(definition);

        Project project = new Project().name("Test");
        projectRepository.saveAndFlush(project);

        Release release = new Release().project(project).versionTag("new");
        release.addDefinitions(definition);
        releaseRepository.saveAndFlush(release);

        project.addReleases(release);
        projectRepository.saveAndFlush(project);

        // Get all the definitions
        restDefinitionMockMvc.perform(get("/api//projects/{projectId}/definitions?sort=id,desc", project.getId()))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(jsonPath("$.[*].id").value(hasItem(definition.getId().intValue())))
                             .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
                             .andExpect(jsonPath("$.[*].originalText").value(hasItem(DEFAULT_ORIGINAL_TEXT)));
    }

    @Test
    @Transactional
    public void getDefinition() throws Exception {
        // Initialize the database
        definitionRepository.saveAndFlush(definition);

        // Get the definition
        restDefinitionMockMvc.perform(get("/api/definitions/{id}", definition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(definition.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.originalText").value(DEFAULT_ORIGINAL_TEXT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDefinition() throws Exception {
        // Get the definition
        restDefinitionMockMvc.perform(get("/api/definitions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDefinition() throws Exception {
        // Initialize the database
        definitionRepository.saveAndFlush(definition);
        int databaseSizeBeforeUpdate = definitionRepository.findAll().size();

        // Update the definition
        Definition updatedDefinition = definitionRepository.findOne(definition.getId());
        updatedDefinition
                .code(UPDATED_CODE)
                .originalText(UPDATED_ORIGINAL_TEXT);
        DefinitionDTO definitionDTO = definitionMapper.definitionToDefinitionDTO(updatedDefinition);

        restDefinitionMockMvc.perform(put("/api/definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(definitionDTO)))
                .andExpect(status().isOk());

        // Validate the Definition in the database
        List<Definition> definitions = definitionRepository.findAll();
        assertThat(definitions).hasSize(databaseSizeBeforeUpdate);
        Definition testDefinition = definitions.get(definitions.size() - 1);
        assertThat(testDefinition.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDefinition.getOriginalText()).isEqualTo(UPDATED_ORIGINAL_TEXT);
    }

    @Test
    @Transactional
    public void updateDefinition_translationGetsMarkedAsUpdateNeeded() throws Exception {
        saveDefinitionWithTranslation();

        int databaseSizeBeforeUpdate = definitionRepository.findAll().size();

        // Update the definition: we need to create a new definition object here or else the cache of the database gets confused
        Definition updatedDefinition = new Definition().code(UPDATED_CODE).originalText(UPDATED_ORIGINAL_TEXT);
        updatedDefinition.setId(definition.getId());
        DefinitionDTO definitionDTO = definitionMapper.definitionToDefinitionDTO(updatedDefinition);

        restDefinitionMockMvc.perform(put("/api/definitions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(definitionDTO)))
                             .andExpect(status().isOk());

        // Validate the Definition in the database
        List<Definition> definitions = definitionRepository.findAll();
        assertThat(definitions).hasSize(databaseSizeBeforeUpdate);
        Definition testDefinition = definitions.get(definitions.size() - 1);
        assertThat(testDefinition.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDefinition.getOriginalText()).isEqualTo(UPDATED_ORIGINAL_TEXT);
        // Assert that all translations have been marked as "update needed"
        List<Translation> translations = translationRepository.findByDefinitionId(definition.getId());
        assertThat(translations).allMatch(Translation::isUpdateNeeded);
    }

    private void saveDefinitionWithTranslation() {
        // Initialize the database
        definitionRepository.saveAndFlush(definition);
        // save translation
        translation.definition(definition);
        translationRepository.saveAndFlush(translation);
        definition.addTranslations(translation);
        definitionRepository.saveAndFlush(definition);
    }


    @Test
    @Transactional
    public void deleteDefinition() throws Exception {
        saveDefinitionWithTranslation();
        int databaseSizeBeforeDelete = definitionRepository.findAll().size();

        // Get the definition
        restDefinitionMockMvc.perform(delete("/api/definitions/{id}", definition.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
                             .andExpect(status().isOk());

        // Validate the database is empty
        List<Definition> definitions = definitionRepository.findAll();
        assertThat(definitions).hasSize(databaseSizeBeforeDelete - 1);
        // Test if the delete cascaded to all the translations of the definition
        List<Translation> translations = translationRepository.findByDefinitionId(definition.getId());
        assertThat(translations).hasSize(0);
    }
}
