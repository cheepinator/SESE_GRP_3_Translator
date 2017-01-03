package com.sese.translator.web.rest;

import com.sese.translator.SeseTranslatorApp;
import com.sese.translator.domain.Language;
import com.sese.translator.domain.Project;
import com.sese.translator.domain.Projectassignment;
import com.sese.translator.domain.Release;
import com.sese.translator.domain.enumeration.Projectrole;
import com.sese.translator.repository.ProjectRepository;
import com.sese.translator.repository.ProjectassignmentRepository;
import com.sese.translator.repository.ReleaseRepository;
import com.sese.translator.service.ProjectService;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.ProjectDTO;
import com.sese.translator.service.mapper.ProjectMapper;
import com.sese.translator.web.rest.util.HeaderUtil;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ProjectResource REST controller.
 *
 * @see ProjectResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeseTranslatorApp.class)
public class ProjectResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private ProjectassignmentRepository projectassignmentRepository;

    @Inject
    private ProjectMapper projectMapper;

    @Inject
    private ProjectService projectService;

    @Inject
    private ReleaseService releaseService;

    @Inject
    private UserService userService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProjectMockMvc;

    private Project project;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectResource projectResource = new ProjectResource();
        ReflectionTestUtils.setField(projectResource, "projectService", projectService);
        ReflectionTestUtils.setField(projectResource, "releaseService", releaseService);
        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectResource)
                                                 .setCustomArgumentResolvers(pageableArgumentResolver)
                                                 .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createEntity(EntityManager em) {
        Project project = new Project()
                .name(DEFAULT_NAME);
        return project;
    }

    @Before
    public void initTest() {
        project = createEntity(em);
        userService.getUserWithAuthoritiesByLogin("user").ifPresent(user -> project.setOwner(user));
    }

    @WithMockUser
    @Test
    @Transactional
    public void createProject() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();
        int releasesBeforeCreate = releaseRepository.findAll().size();

        // delete owner of the project
        project.setOwner(null);

        // Create the Project
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);

        restProjectMockMvc.perform(post("/api/projects")
                                       .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                       .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                          .andExpect(status().isCreated());

        // Validate the Project in the database
        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeCreate + 1);
        Project testProject = projects.get(projects.size() - 1);
        assertThat(testProject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProject.getOwner().getLogin()).isEqualTo("user"); // 'user' is the default mocked username

        // Assert a default release was created for the project
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(releasesBeforeCreate + 1);
        Release defaultRelease = releases.get(releases.size() - 1);
        assertThat(defaultRelease.getProject()).isEqualTo(testProject);
        assertThat(defaultRelease.getVersionTag()).isEqualTo(Release.DEFAULT_TAG);
        assertThat(defaultRelease.getDueDate()).isNull();

        // Assert a default language was created for the release
        assertThat(defaultRelease.getLanguages()).hasSize(1);
        assertThat(defaultRelease.getLanguages().iterator().next().getCode()).isEqualTo(Language.DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set the field null
        project.setName(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);

        restProjectMockMvc.perform(post("/api/projects")
                                       .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                       .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                          .andExpect(status().isBadRequest());

        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOwnerCantBeSet() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);

        restProjectMockMvc.perform(post("/api/projects")
                                       .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                       .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                          .andExpect(status().isBadRequest())
                          .andExpect(header().string(HeaderUtil.X_SESE_TRANSLATOR_APP_ERROR, "A new project cannot already have an Owner"));

        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIdCantBeSet() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set id
        project.setId(1L);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);

        restProjectMockMvc.perform(post("/api/projects")
                                       .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                       .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                          .andExpect(status().isBadRequest())
                          .andExpect(header().string(HeaderUtil.X_SESE_TRANSLATOR_APP_ERROR, "A new project cannot already have an ID"));

        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeTest);
    }

    @WithMockUser
    @Test
    @Transactional
    public void getAllProjects_includingAssignedOnes() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        // create project, the current user is only assigned to, not the owner of
        String projectNameForAssignedProject = "Test";
        Project testProject = new Project().name(projectNameForAssignedProject).owner(null);
        projectRepository.saveAndFlush(testProject);
        Projectassignment projectassignment = new Projectassignment().assignedProject(testProject).role(Projectrole.TRANSLATOR);
        userService.getUserWithAuthoritiesByLogin("user").ifPresent(projectassignment::setAssignedUser);
        projectassignmentRepository.saveAndFlush(projectassignment);

        // Get all the projects the current user owns including the project the user is only assigned to
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc"))
                          .andExpect(status().isOk())
                          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                          .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
                          .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                          .andExpect(jsonPath("$.[*].id").value(hasItem(testProject.getId().intValue())))
                          .andExpect(jsonPath("$.[*].name").value(hasItem(projectNameForAssignedProject)));
    }

    @WithMockUser
    @Test
    @Transactional
    public void getAllProjects_ignoresProjectsNotOwnedByCurrentUser() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        // create project not owned by current user
        String projectNameForOtherUser = "Test";
        projectRepository.saveAndFlush(new Project().name(projectNameForOtherUser).owner(null));

        // Get all the projects
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc"))
                          .andExpect(status().isOk())
                          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                          .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
                          .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                          .andExpect(jsonPath("$.[*].name").value(not(hasItem(projectNameForOtherUser))));
    }

    @WithMockUser
    @Test
    @Transactional
    public void getAllProjects_ignoresProjectsNotAssignedToCurrentUser() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        // create project not owned by current user
        String projectNameForOtherUser = "Test";
        projectRepository.saveAndFlush(new Project().name(projectNameForOtherUser).owner(null));

        // Get all the projects
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc"))
                          .andExpect(status().isOk())
                          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                          .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
                          .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                          .andExpect(jsonPath("$.[*].name").value(not(hasItem(projectNameForOtherUser))));
    }

    @Test
    @Transactional
    @WithMockUser
    public void getProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", project.getId()))
                          .andExpect(status().isOk())
                          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                          .andExpect(jsonPath("$.id").value(project.getId().intValue()))
                          .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    @WithMockUser
    public void getNonExistingProject() throws Exception {
        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", Long.MAX_VALUE))
                          .andExpect(status().isForbidden());
    }

    @WithMockUser
    @Test
    @Transactional
    public void updateProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project
        Project updatedProject = projectRepository.findOne(project.getId());
        updatedProject.name(UPDATED_NAME);
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(updatedProject);

        restProjectMockMvc.perform(put("/api/projects")
                                       .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                       .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                          .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projects.get(projects.size() - 1);
        assertThat(testProject.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        int databaseSizeBeforeDelete = projectRepository.findAll().size();

        // Get the project
        restProjectMockMvc.perform(delete("/api/projects/{id}", project.getId())
                                       .accept(TestUtil.APPLICATION_JSON_UTF8))
                          .andExpect(status().isOk());

        // Validate the database is empty
        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeDelete - 1);
    }
}
