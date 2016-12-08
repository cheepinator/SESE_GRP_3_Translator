package com.sese.translator.web.rest;

import com.sese.translator.SeseTranslatorApp;
import com.sese.translator.domain.*;
import com.sese.translator.repository.*;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.dto.TranslationDTO;
import com.sese.translator.service.mapper.TranslationMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
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
 * Test class for the TranslationResource REST controller.
 *
 * @see TranslationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeseTranslatorApp.class)
public class TranslationResourceIntTest {

    private static final String DEFAULT_TRANSLATED_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TRANSLATED_TEXT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_UPDATE_NEEDED = false;
    private static final Boolean UPDATED_UPDATE_NEEDED = true;

    @Inject
    private TranslationRepository translationRepository;

    @Inject
    private DefinitionRepository definitionRepository;

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private TranslationMapper translationMapper;

    @Inject
    private TranslationService translationService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTranslationMockMvc;

    private Translation translation;

    private Language language;
    private Definition definition;

    @Before
    public void setup() {
        language = new Language();
        language.setCode("TEST");
        language = languageRepository.save(language);
        definition = new Definition();
        definition.setCode("code");
        definition.setOriginalText("jkfldösafdsa");
        definitionRepository.save(definition);
        MockitoAnnotations.initMocks(this);
        TranslationResource translationResource = new TranslationResource();
        ReflectionTestUtils.setField(translationResource, "translationService", translationService);
        this.restTranslationMockMvc = MockMvcBuilders.standaloneSetup(translationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Translation createEntity(EntityManager em) {
        Translation translation = new Translation()
            .translatedText(DEFAULT_TRANSLATED_TEXT)
            .updateNeeded(DEFAULT_UPDATE_NEEDED);
        return translation;
    }

    @Before
    public void initTest() {
        translation = createEntity(em);
    }

    @Test
    @Transactional
    public void createTranslation() throws Exception {
        int databaseSizeBeforeCreate = translationRepository.findAll().size();

        translation.setDefinition(definition);
        translation.setLanguage(language);
        // Create the Translation
        TranslationDTO translationDTO = translationMapper.translationToTranslationDTO(translation);

        restTranslationMockMvc.perform(post("/api/translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(translationDTO)))
            .andExpect(status().isCreated());

        // Validate the Translation in the database
        List<Translation> translations = translationRepository.findAll();
        assertThat(translations).hasSize(databaseSizeBeforeCreate + 1);
        Translation testTranslation = translations.get(translations.size() - 1);
        assertThat(testTranslation.getTranslatedText()).isEqualTo(DEFAULT_TRANSLATED_TEXT);
        assertThat(testTranslation.isUpdateNeeded()).isEqualTo(DEFAULT_UPDATE_NEEDED);
    }

    @Test
    @Transactional
    public void checkUpdateNeededIsRequired() throws Exception {
        int databaseSizeBeforeTest = translationRepository.findAll().size();
        // set the field null
        translation.setUpdateNeeded(null);

        // Create the Translation, which fails.
        TranslationDTO translationDTO = translationMapper.translationToTranslationDTO(translation);

        restTranslationMockMvc.perform(post("/api/translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(translationDTO)))
            .andExpect(status().isBadRequest());

        List<Translation> translations = translationRepository.findAll();
        assertThat(translations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTranslations() throws Exception {
        // Initialize the database
        translationRepository.saveAndFlush(translation);

        // Get all the translations
        restTranslationMockMvc.perform(get("/api/translations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(translation.getId().intValue())))
            .andExpect(jsonPath("$.[*].translatedText").value(hasItem(DEFAULT_TRANSLATED_TEXT.toString())))
            .andExpect(jsonPath("$.[*].updateNeeded").value(hasItem(DEFAULT_UPDATE_NEEDED.booleanValue())));
    }

    @Test
    @Transactional
    public void getAllTranslations_forDefinition() throws Exception {
        // Initialize the database with a definition and corresponding translation
        translationRepository.saveAndFlush(translation);
        Definition definition = new Definition().code("code").originalText("text").addTranslations(translation);
        definitionRepository.saveAndFlush(definition);
        translation.definition(definition);
        translationRepository.saveAndFlush(translation);

        // Get all the translations for the definition
        restTranslationMockMvc.perform(get("/api/definitions/{id}/translations?sort=id,desc", definition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(translation.getId().intValue())))
            .andExpect(jsonPath("$.[*].translatedText").value(hasItem(DEFAULT_TRANSLATED_TEXT)))
            .andExpect(jsonPath("$.[*].updateNeeded").value(hasItem(DEFAULT_UPDATE_NEEDED)));
    }

    @Test
    @Transactional
    public void getAllTranslations_forDefinition_noTranslationsAvailable() throws Exception {
        // Initialize the database with a definition that has no translations
        Definition definition = new Definition().code("code").originalText("text").addTranslations(translation);
        definitionRepository.saveAndFlush(definition);

        // Get all the translations for the definition -> there should be none
        restTranslationMockMvc.perform(get("/api/definitions/{id}/translations?sort=id,desc", definition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @Test
    @Transactional
    public void getAllTranslations_forProject() throws Exception {
        // Initialize the database with a definition and corresponding translation as well as a project and release
        translationRepository.saveAndFlush(translation);

        Project aProject = new Project().name("aProject");
        projectRepository.saveAndFlush(aProject);

        Release aRelease = new Release().versionTag("aRelease").project(aProject);
        releaseRepository.saveAndFlush(aRelease);

        Definition definition = new Definition().code("code").originalText("text").addTranslations(translation).release(aRelease);
        definitionRepository.saveAndFlush(definition);

        translation.definition(definition);
        translationRepository.saveAndFlush(translation);

        aRelease.addDefinitions(definition);
        releaseRepository.saveAndFlush(aRelease);

        aProject.addReleases(aRelease);
        projectRepository.saveAndFlush(aProject);

        // Get all the translations for the definition
        restTranslationMockMvc.perform(get("/api/projects/{projectId}/translations", aProject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(translation.getId().intValue())))
            .andExpect(jsonPath("$.[*].translatedText").value(hasItem(DEFAULT_TRANSLATED_TEXT)))
            .andExpect(jsonPath("$.[*].updateNeeded").value(hasItem(DEFAULT_UPDATE_NEEDED)));
    }

    @Test
    @Transactional
    public void getTranslation() throws Exception {
        // Initialize the database
        translationRepository.saveAndFlush(translation);

        // Get the translation
        restTranslationMockMvc.perform(get("/api/translations/{id}", translation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(translation.getId().intValue()))
            .andExpect(jsonPath("$.translatedText").value(DEFAULT_TRANSLATED_TEXT.toString()))
            .andExpect(jsonPath("$.updateNeeded").value(DEFAULT_UPDATE_NEEDED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTranslation() throws Exception {
        // Get the translation
        restTranslationMockMvc.perform(get("/api/translations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser
    public void updateTranslation() throws Exception {
        // Initialize the database
        translation.setDefinition(definition);
        translation.setLanguage(language);
        translationRepository.saveAndFlush(translation);
        int databaseSizeBeforeUpdate = translationRepository.findAll().size();

        // Update the translation
        Translation updatedTranslation = translationRepository.findOne(translation.getId());
        updatedTranslation
            .translatedText(UPDATED_TRANSLATED_TEXT)
            .updateNeeded(UPDATED_UPDATE_NEEDED)
            .language(language)
            .definition(definition);
        TranslationDTO translationDTO = translationMapper.translationToTranslationDTO(updatedTranslation);

        restTranslationMockMvc.perform(put("/api/translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(translationDTO)))
            .andExpect(status().isOk());

        // Validate the Translation in the database
        List<Translation> translations = translationRepository.findAll();
        assertThat(translations).hasSize(databaseSizeBeforeUpdate);
        Translation testTranslation = translations.get(translations.size() - 1);
        assertThat(testTranslation.getTranslatedText()).isEqualTo(UPDATED_TRANSLATED_TEXT);
        assertThat(testTranslation.isUpdateNeeded()).isEqualTo(false);//updated translation is not needed to be updated anymore
    }

    @Test
    @Transactional
    public void deleteTranslation() throws Exception {
        // Initialize the database
        translationRepository.saveAndFlush(translation);
        int databaseSizeBeforeDelete = translationRepository.findAll().size();

        // Get the translation
        restTranslationMockMvc.perform(delete("/api/translations/{id}", translation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Translation> translations = translationRepository.findAll();
        assertThat(translations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
