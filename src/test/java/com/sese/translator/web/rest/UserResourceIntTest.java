package com.sese.translator.web.rest;

import com.sese.translator.SeseTranslatorApp;
import com.sese.translator.domain.User;
import com.sese.translator.repository.AuthorityRepository;
import com.sese.translator.repository.UserRepository;
import com.sese.translator.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeseTranslatorApp.class)
@Transactional
public class UserResourceIntTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private AuthorityRepository authorityRepository;

    private MockMvc restUserMockMvc;

    /**
     * Create a User.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which has a required relationship to the User entity.
     */
    public static User createEntity(EntityManager em) {
        User user = new User();
        user.setLogin("test");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail("test@test.com");
        user.setFirstName("test");
        user.setLastName("test");
        user.setLangKey("en");
        em.persist(user);
        em.flush();
        return user;
    }

    @Before
    public void setup() {
        UserResource userResource = new UserResource();
        ReflectionTestUtils.setField(userResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(userResource, "userService", userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource).build();
    }

    @Test
    public void testGetExistingUser() throws Exception {

        restUserMockMvc.perform(get("/api/users/admin")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.lastName").value("Administrator"));
    }

    @Test
    public void testGetExistingUser_nameOnly() throws Exception {
        User user = userService.getUserWithAuthoritiesByLogin("user")
                               .orElseThrow(() -> new IllegalStateException("Could not find User 'user'"));

        restUserMockMvc.perform(get("/api/users/{id}/name", user.getId())
            .accept(MediaType.APPLICATION_JSON))
                       .andExpect(status().isOk())
                       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                       .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
