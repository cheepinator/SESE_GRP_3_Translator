package com.sese.translator.web.rest;

import com.sese.translator.SeseTranslatorApp;

import com.sese.translator.domain.Translation;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.service.TranslationService;
import com.sese.translator.service.dto.TranslationDTO;
import com.sese.translator.service.mapper.TranslationMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Inject
    private TranslationRepository translationRepository;

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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TranslationResource translationResource = new TranslationResource();
        ReflectionTestUtils.setField(translationResource, "translationService", translationService);
        this.restTranslationMockMvc = MockMvcBuilders.standaloneSetup(translationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Translation createEntity(EntityManager em) {
        Translation translation = new Translation()
                .translatedText(DEFAULT_TRANSLATED_TEXT);
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
                .andExpect(jsonPath("$.[*].translatedText").value(hasItem(DEFAULT_TRANSLATED_TEXT.toString())));
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
            .andExpect(jsonPath("$.translatedText").value(DEFAULT_TRANSLATED_TEXT.toString()));
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
    public void updateTranslation() throws Exception {
        // Initialize the database
        translationRepository.saveAndFlush(translation);
        int databaseSizeBeforeUpdate = translationRepository.findAll().size();

        // Update the translation
        Translation updatedTranslation = translationRepository.findOne(translation.getId());
        updatedTranslation
                .translatedText(UPDATED_TRANSLATED_TEXT);
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
