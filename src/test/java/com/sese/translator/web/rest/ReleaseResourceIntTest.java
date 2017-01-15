package com.sese.translator.web.rest;

import com.sese.translator.SeseTranslatorApp;
import com.sese.translator.domain.Project;
import com.sese.translator.domain.Release;
import com.sese.translator.repository.AuthorityRepository;
import com.sese.translator.repository.ProjectRepository;
import com.sese.translator.repository.ReleaseRepository;
import com.sese.translator.service.ProjectService;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.ReleaseDTO;
import com.sese.translator.service.mapper.ProjectMapper;
import com.sese.translator.service.mapper.ReleaseMapper;
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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ReleaseResource REST controller.
 *
 * @see ReleaseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeseTranslatorApp.class)
public class ReleaseResourceIntTest {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_TAG = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_TAG = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DUE_DATE_STR = DateTimeFormatter.ISO_INSTANT.format(DEFAULT_DUE_DATE);
    private static Project testProject;
    @Inject
    private ReleaseRepository releaseRepository;
    @Inject
    private ProjectRepository projectRepository;
    @Inject
    private ReleaseMapper releaseMapper;
    @Inject
    private ProjectMapper projectMapper;
    @Inject
    private ReleaseService releaseService;
    @Inject
    private ProjectService projectService;
    @Inject
    private UserService userService;
    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;
    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
    @Inject
    private EntityManager em;
    @Inject
    private AuthorityRepository authorityRepository;
    private MockMvc restReleaseMockMvc;
    private Release release;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Release createEntity(EntityManager em) {
        testProject = new Project().name("TestProject");
        em.persist(testProject);
        Release release = new Release()
            .description(DEFAULT_DESCRIPTION)
            .versionTag(DEFAULT_VERSION_TAG)
            .dueDate(DEFAULT_DUE_DATE)
            .project(testProject);
        return release;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReleaseResource releaseResource = new ReleaseResource();
        ReflectionTestUtils.setField(releaseResource, "releaseService", releaseService);
        ReflectionTestUtils.setField(releaseResource, "projectService", projectService);
        this.restReleaseMockMvc = MockMvcBuilders.standaloneSetup(releaseResource)
                                                 .setCustomArgumentResolvers(pageableArgumentResolver)
                                                 .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        release = createEntity(em);
        userService.getUserWithAuthoritiesByLogin("user").ifPresent(user -> testProject.setOwner(user));
    }

    @Test
    @Transactional
    public void createRelease() throws Exception {
        int databaseSizeBeforeCreate = releaseRepository.findAll().size();

        // Create the Release
        ReleaseDTO releaseDTO = releaseMapper.releaseToReleaseDTO(release);

        restReleaseMockMvc.perform(post("/api/releases")
                                       .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                       .content(TestUtil.convertObjectToJsonBytes(releaseDTO)))
                          .andExpect(status().isCreated());

        // Validate the Release in the database
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeCreate + 1);
        Release testRelease = releases.get(releases.size() - 1);
        assertThat(testRelease.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRelease.getVersionTag()).isEqualTo(DEFAULT_VERSION_TAG);
        assertThat(testRelease.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
    }

    @Test
    @Transactional
    public void checkVersionTagIsRequired() throws Exception {
        int databaseSizeBeforeTest = releaseRepository.findAll().size();
        // set the field null
        release.setVersionTag(null);

        // Create the Release, which fails.
        ReleaseDTO releaseDTO = releaseMapper.releaseToReleaseDTO(release);

        restReleaseMockMvc.perform(post("/api/releases")
                                       .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                       .content(TestUtil.convertObjectToJsonBytes(releaseDTO)))
                          .andExpect(status().isBadRequest());

        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser
    public void getAllReleases() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);

        // Get all the releases
        restReleaseMockMvc.perform(get("/api/releases?sort=id,desc"))
                          .andExpect(status().isOk())
                          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                          .andExpect(jsonPath("$.[*].id").value(hasItem(release.getId().intValue())))
                          .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                          .andExpect(jsonPath("$.[*].versionTag").value(hasItem(DEFAULT_VERSION_TAG.toString())))
                          .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE_STR)));
    }

    @Test
    @Transactional
    @WithMockUser
    public void getRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        //projectRepository.saveAndFlush(testProject);
        // Get the release
        restReleaseMockMvc.perform(get("/api/releases/{id}", release.getId()))
                          .andExpect(status().isOk())
                          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                          .andExpect(jsonPath("$.id").value(release.getId().intValue()))
                          .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
                          .andExpect(jsonPath("$.versionTag").value(DEFAULT_VERSION_TAG.toString()))
                          .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE_STR));
    }

    @Test
    @Transactional
    @WithMockUser
    public void getDefaultRelease() throws Exception {
        // Initialize the database
        ReleaseDTO defaultRelease = releaseService.createDefaultRelease(projectMapper.projectToProjectDTO(testProject));

        // Get the default release
        restReleaseMockMvc.perform(get("/api/projects/{projectId}/releases/default", testProject.getId()))
                          .andExpect(status().isOk())
                          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                          .andExpect(jsonPath("$.id").value(defaultRelease.getId().intValue()))
                          .andExpect(jsonPath("$.versionTag").value(defaultRelease.getVersionTag()));
    }

    @Test
    @Transactional
    @WithMockUser
    public void getAllReleasesForProject() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);

        // Get all the releases
        restReleaseMockMvc.perform(get("/api/projects/{projectId}/releases?sort=id,desc", testProject.getId()))
                          .andExpect(status().isOk())
                          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                          .andExpect(jsonPath("$.[*].id").value(hasItem(release.getId().intValue())))
                          .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                          .andExpect(jsonPath("$.[*].versionTag").value(hasItem(DEFAULT_VERSION_TAG)))
                          .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE_STR)));
    }

    @Test
    @Transactional
    @WithMockUser
    public void getNonExistingRelease() throws Exception {
        // Get the release
        restReleaseMockMvc.perform(get("/api/releases/{id}", Long.MAX_VALUE))
                          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        int databaseSizeBeforeUpdate = releaseRepository.findAll().size();

        // Update the release
        Release updatedRelease = releaseRepository.findOne(release.getId());
        updatedRelease
            .description(UPDATED_DESCRIPTION)
            .versionTag(UPDATED_VERSION_TAG)
            .dueDate(UPDATED_DUE_DATE);
        ReleaseDTO releaseDTO = releaseMapper.releaseToReleaseDTO(updatedRelease);

        restReleaseMockMvc.perform(put("/api/releases")
                                       .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                       .content(TestUtil.convertObjectToJsonBytes(releaseDTO)))
                          .andExpect(status().isOk());

        // Validate the Release in the database
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeUpdate);
        Release testRelease = releases.get(releases.size() - 1);
        assertThat(testRelease.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRelease.getVersionTag()).isEqualTo(UPDATED_VERSION_TAG);
        assertThat(testRelease.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    public void deleteRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        int databaseSizeBeforeDelete = releaseRepository.findAll().size();

        // Get the release
        restReleaseMockMvc.perform(delete("/api/releases/{id}", release.getId())
                                       .accept(TestUtil.APPLICATION_JSON_UTF8))
                          .andExpect(status().isOk());

        // Validate the database is empty
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeDelete - 1);
    }
}
