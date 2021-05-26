package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.repository.YearSessionRepository;
import com.thirdcc.webapp.service.YearSessionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link YearSessionResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class YearSessionResourceIT {

    private static final String ENTITY_API_URL = "/api/year-sessions";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    @Autowired
    private YearSessionRepository yearSessionRepository;

    @Autowired
    private YearSessionService yearSessionService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restYearSessionMockMvc;

    private YearSession yearSession;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static YearSession createEntity(EntityManager em) {
        YearSession yearSession = new YearSession()
            .value(DEFAULT_VALUE);
        return yearSession;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static YearSession createUpdatedEntity(EntityManager em) {
        YearSession yearSession = new YearSession()
            .value(UPDATED_VALUE);
        return yearSession;
    }

    @BeforeEach
    public void initTest() {
        yearSession = createEntity(em);
    }

    @Test
    @Transactional
    public void createYearSession() throws Exception {
        int databaseSizeBeforeCreate = yearSessionRepository.findAll().size();

        // Create the YearSession
        restYearSessionMockMvc.perform(post("/api/year-sessions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(yearSession)))
            .andExpect(status().isCreated());

        // Validate the YearSession in the database
        List<YearSession> yearSessionList = yearSessionRepository.findAll();
        assertThat(yearSessionList).hasSize(databaseSizeBeforeCreate + 1);
        YearSession testYearSession = yearSessionList.get(yearSessionList.size() - 1);
        assertThat(testYearSession.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createYearSessionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = yearSessionRepository.findAll().size();

        // Create the YearSession with an existing ID
        yearSession.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restYearSessionMockMvc.perform(post("/api/year-sessions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(yearSession)))
            .andExpect(status().isBadRequest());

        // Validate the YearSession in the database
        List<YearSession> yearSessionList = yearSessionRepository.findAll();
        assertThat(yearSessionList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllYearSessions() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        // Get all the yearSessionList
        restYearSessionMockMvc.perform(get("/api/year-sessions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(yearSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }


    @Test
    @Transactional
    void getYearSessionsByIdFiltering() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        Long id = yearSession.getId();

        defaultYearSessionShouldBeFound("id.equals=" + id);
        defaultYearSessionShouldNotBeFound("id.notEquals=" + id);

        defaultYearSessionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultYearSessionShouldNotBeFound("id.greaterThan=" + id);

        defaultYearSessionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultYearSessionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllYearSessionsByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        // Get all the yearSessionList where value equals to DEFAULT_VALUE
        defaultYearSessionShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the yearSessionList where value equals to UPDATED_VALUE
        defaultYearSessionShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllYearSessionsByValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        // Get all the yearSessionList where value not equals to DEFAULT_VALUE
        defaultYearSessionShouldNotBeFound("value.notEquals=" + DEFAULT_VALUE);

        // Get all the yearSessionList where value not equals to UPDATED_VALUE
        defaultYearSessionShouldBeFound("value.notEquals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllYearSessionsByValueIsInShouldWork() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        // Get all the yearSessionList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultYearSessionShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the yearSessionList where value equals to UPDATED_VALUE
        defaultYearSessionShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllYearSessionsByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        // Get all the yearSessionList where value is not null
        defaultYearSessionShouldBeFound("value.specified=true");

        // Get all the yearSessionList where value is null
        defaultYearSessionShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllYearSessionsByValueContainsSomething() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        // Get all the yearSessionList where value contains DEFAULT_VALUE
        defaultYearSessionShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the yearSessionList where value contains UPDATED_VALUE
        defaultYearSessionShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllYearSessionsByValueNotContainsSomething() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        // Get all the yearSessionList where value does not contain DEFAULT_VALUE
        defaultYearSessionShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the yearSessionList where value does not contain UPDATED_VALUE
        defaultYearSessionShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultYearSessionShouldBeFound(String filter) throws Exception {
        restYearSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(yearSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));

        // Check, that the count call also returns 1
        restYearSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultYearSessionShouldNotBeFound(String filter) throws Exception {
        restYearSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restYearSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getYearSession() throws Exception {
        // Initialize the database
        yearSessionRepository.saveAndFlush(yearSession);

        // Get the yearSession
        restYearSessionMockMvc.perform(get("/api/year-sessions/{id}", yearSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(yearSession.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingYearSession() throws Exception {
        // Get the yearSession
        restYearSessionMockMvc.perform(get("/api/year-sessions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateYearSession() throws Exception {
        // Initialize the database
        yearSessionService.save(yearSession);

        int databaseSizeBeforeUpdate = yearSessionRepository.findAll().size();

        // Update the yearSession
        YearSession updatedYearSession = yearSessionRepository.findById(yearSession.getId()).get();
        // Disconnect from session so that the updates on updatedYearSession are not directly saved in db
        em.detach(updatedYearSession);
        updatedYearSession
            .value(UPDATED_VALUE);

        restYearSessionMockMvc.perform(put("/api/year-sessions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedYearSession)))
            .andExpect(status().isOk());

        // Validate the YearSession in the database
        List<YearSession> yearSessionList = yearSessionRepository.findAll();
        assertThat(yearSessionList).hasSize(databaseSizeBeforeUpdate);
        YearSession testYearSession = yearSessionList.get(yearSessionList.size() - 1);
        assertThat(testYearSession.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void updateNonExistingYearSession() throws Exception {
        int databaseSizeBeforeUpdate = yearSessionRepository.findAll().size();

        // Create the YearSession

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restYearSessionMockMvc.perform(put("/api/year-sessions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(yearSession)))
            .andExpect(status().isBadRequest());

        // Validate the YearSession in the database
        List<YearSession> yearSessionList = yearSessionRepository.findAll();
        assertThat(yearSessionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteYearSession() throws Exception {
        // Initialize the database
        yearSessionService.save(yearSession);

        int databaseSizeBeforeDelete = yearSessionRepository.findAll().size();

        // Delete the yearSession
        restYearSessionMockMvc.perform(delete("/api/year-sessions/{id}", yearSession.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<YearSession> yearSessionList = yearSessionRepository.findAll();
        assertThat(yearSessionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(YearSession.class);
        YearSession yearSession1 = new YearSession();
        yearSession1.setId(1L);
        YearSession yearSession2 = new YearSession();
        yearSession2.setId(yearSession1.getId());
        assertThat(yearSession1).isEqualTo(yearSession2);
        yearSession2.setId(2L);
        assertThat(yearSession1).isNotEqualTo(yearSession2);
        yearSession1.setId(null);
        assertThat(yearSession1).isNotEqualTo(yearSession2);
    }
}
