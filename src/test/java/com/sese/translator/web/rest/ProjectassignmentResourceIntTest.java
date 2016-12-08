package com.sese.translator.web.rest;

import com.sese.translator.SeseTranslatorApp;
import com.sese.translator.domain.Project;
import com.sese.translator.domain.Projectassignment;
import com.sese.translator.domain.enumeration.Projectrole;
import com.sese.translator.repository.ProjectassignmentRepository;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.ProjectassignmentDTO;
import com.sese.translator.service.mapper.ProjectassignmentMapper;
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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Test class for the ProjectassignmentResource REST controller.
 *
 * @see ProjectassignmentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeseTranslatorApp.class)
public class ProjectassignmentResourceIntTest {

    private static final Projectrole DEFAULT_ROLE = Projectrole.RELEASE_MANAGER;
    private static final Projectrole UPDATED_ROLE = Projectrole.TRANSLATOR;

    @Inject
    private ProjectassignmentRepository projectassignmentRepository;

    @Inject
    private UserService userService;

    @Inject
    private ProjectassignmentMapper projectassignmentMapper;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProjectassignmentMockMvc;

    private Projectassignment projectassignment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectassignmentResource projectassignmentResource = new ProjectassignmentResource();
        ReflectionTestUtils.setField(projectassignmentResource, "projectassignmentRepository", projectassignmentRepository);
        ReflectionTestUtils.setField(projectassignmentResource, "projectassignmentMapper", projectassignmentMapper);
        ReflectionTestUtils.setField(projectassignmentResource, "userService", userService);
        this.restProjectassignmentMockMvc = MockMvcBuilders.standaloneSetup(projectassignmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Projectassignment createEntity(EntityManager em) {
        Project p = new Project().name("test");
        em.persist(p);
        em.flush();

        return new Projectassignment()
                .role(DEFAULT_ROLE).assignedProject(p);
    }

    @Before
    public void initTest() {
        projectassignment = createEntity(em);
        userService.getUserWithAuthoritiesByLogin("user").ifPresent(user -> {
            projectassignment.getAssignedProject().setOwner(user);
            projectassignment.setAssignedUser(user);
        });
    }

    @Test
    @Transactional
    public void createProjectassignment() throws Exception {
        int databaseSizeBeforeCreate = projectassignmentRepository.findAll().size();

        // Create the Projectassignment
        ProjectassignmentDTO projectassignmentDTO = projectassignmentMapper.projectassignmentToProjectassignmentDTO(projectassignment);

        restProjectassignmentMockMvc.perform(post("/api/projectassignments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectassignmentDTO)))
                .andExpect(status().isCreated());

        // Validate the Projectassignment in the database
        List<Projectassignment> projectassignments = projectassignmentRepository.findAll();
        assertThat(projectassignments).hasSize(databaseSizeBeforeCreate + 1);
        Projectassignment testProjectassignment = projectassignments.get(projectassignments.size() - 1);
        assertThat(testProjectassignment.getRole()).isEqualTo(DEFAULT_ROLE);
    }

    @Test
    @Transactional
    public void checkRoleIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectassignmentRepository.findAll().size();
        // set the field null
        projectassignment.setRole(null);

        // Create the Projectassignment, which fails.
        ProjectassignmentDTO projectassignmentDTO = projectassignmentMapper.projectassignmentToProjectassignmentDTO(projectassignment);

        restProjectassignmentMockMvc.perform(post("/api/projectassignments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectassignmentDTO)))
                .andExpect(status().isBadRequest());

        List<Projectassignment> projectassignments = projectassignmentRepository.findAll();
        assertThat(projectassignments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser
    public void getAllProjectassignments() throws Exception {
        // Initialize the database
        projectassignmentRepository.saveAndFlush(projectassignment);

        // Get all the projectassignments
        restProjectassignmentMockMvc.perform(get("/api/projectassignments?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(projectassignment.getId().intValue())))
                .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    public void getProjectassignment() throws Exception {
        // Initialize the database
        projectassignmentRepository.saveAndFlush(projectassignment);

        // Get the projectassignment
        restProjectassignmentMockMvc.perform(get("/api/projectassignments/{id}", projectassignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(projectassignment.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    public void getProjectassignment_forProject() throws Exception {
        // Initialize the database
        projectassignmentRepository.saveAndFlush(projectassignment);

        // Get the projectassignment
        restProjectassignmentMockMvc.perform(get("/api/projects/{projectId}/projectassignments", projectassignment.getAssignedProject().getId()))
                                    .andExpect(status().isOk())
                                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                    .andExpect(jsonPath("$.[*].id").value(hasItem(projectassignment.getId().intValue())))
                                    .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    @WithMockUser
    public void getUserRoles_forProject() throws Exception {
        // Initialize the database
        projectassignmentRepository.saveAndFlush(projectassignment);

        // Get the user roles for the current user as string
        restProjectassignmentMockMvc.perform(get("/api//projects/{projectId}/userRoles", projectassignment.getAssignedProject().getId()))
                                    .andExpect(status().isOk())
                                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                    .andExpect(jsonPath("$.[*]").value(hasSize(1)))
                                    .andExpect(jsonPath("$.[*]").value(hasItem(projectassignment.getRole().name())));
    }

    @Test
    @Transactional
    public void getNonExistingProjectassignment() throws Exception {
        // Get the projectassignment
        restProjectassignmentMockMvc.perform(get("/api/projectassignments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectassignment() throws Exception {
        // Initialize the database
        projectassignmentRepository.saveAndFlush(projectassignment);
        int databaseSizeBeforeUpdate = projectassignmentRepository.findAll().size();

        // Update the projectassignment
        Projectassignment updatedProjectassignment = projectassignmentRepository.findOne(projectassignment.getId());
        updatedProjectassignment
                .role(UPDATED_ROLE);
        ProjectassignmentDTO projectassignmentDTO = projectassignmentMapper.projectassignmentToProjectassignmentDTO(updatedProjectassignment);

        restProjectassignmentMockMvc.perform(put("/api/projectassignments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectassignmentDTO)))
                .andExpect(status().isOk());

        // Validate the Projectassignment in the database
        List<Projectassignment> projectassignments = projectassignmentRepository.findAll();
        assertThat(projectassignments).hasSize(databaseSizeBeforeUpdate);
        Projectassignment testProjectassignment = projectassignments.get(projectassignments.size() - 1);
        assertThat(testProjectassignment.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    public void deleteProjectassignment() throws Exception {
        // Initialize the database
        projectassignmentRepository.saveAndFlush(projectassignment);
        int databaseSizeBeforeDelete = projectassignmentRepository.findAll().size();

        // Get the projectassignment
        restProjectassignmentMockMvc.perform(delete("/api/projectassignments/{id}", projectassignment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Projectassignment> projectassignments = projectassignmentRepository.findAll();
        assertThat(projectassignments).hasSize(databaseSizeBeforeDelete - 1);
    }
}
