package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Administrator;
import com.thirdcc.webapp.repository.AdministratorRepository;
import com.thirdcc.webapp.service.AdministratorQueryService;
import com.thirdcc.webapp.service.AdministratorService;
import com.thirdcc.webapp.service.dto.AdministratorDTO;
import com.thirdcc.webapp.service.mapper.AdministratorMapper;

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

import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;

/**
 * Integration tests for the {@Link AdministratorResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class AdministratorResourceIT {

    private static final String ENTITY_API_URL = "/api/administrators";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long SMALLER_USER_ID = DEFAULT_USER_ID - 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_YEAR_SESSION = "AAAAAAAAAA";
    private static final String UPDATED_YEAR_SESSION = "BBBBBBBBBB";

    private static final AdministratorRole DEFAULT_ROLE = AdministratorRole.CC_HEAD;
    private static final AdministratorRole UPDATED_ROLE = AdministratorRole.VICE_CC_HEAD;

    private static final AdministratorStatus DEFAULT_STATUS = AdministratorStatus.ACTIVE;
    private static final AdministratorStatus UPDATED_STATUS = AdministratorStatus.DEACTIVATE;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private AdministratorMapper administratorMapper;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private AdministratorQueryService administratorQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdministratorMockMvc;

    private Administrator administrator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AdministratorResource administratorResource = new AdministratorResource(administratorService, administratorQueryService);
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Administrator createEntity(EntityManager em) {
        Administrator administrator = new Administrator()
            .userId(DEFAULT_USER_ID)
            .yearSession(DEFAULT_YEAR_SESSION)
            .role(DEFAULT_ROLE)
            .status(DEFAULT_STATUS);
        return administrator;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Administrator createUpdatedEntity(EntityManager em) {
        Administrator administrator = new Administrator()
            .userId(UPDATED_USER_ID)
            .yearSession(UPDATED_YEAR_SESSION)
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS);
        return administrator;
    }

    @BeforeEach
    public void initTest() {
        administrator = createEntity(em);
    }

    @Test
    @Transactional
    public void createAdministrator() throws Exception {
        int databaseSizeBeforeCreate = administratorRepository.findAll().size();

        // Create the Administrator
        AdministratorDTO administratorDTO = administratorMapper.toDto(administrator);
        restAdministratorMockMvc.perform(post("/api/administrators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(administratorDTO)))
            .andExpect(status().isCreated());

        // Validate the Administrator in the database
        List<Administrator> administratorList = administratorRepository.findAll();
        assertThat(administratorList).hasSize(databaseSizeBeforeCreate + 1);
        Administrator testAdministrator = administratorList.get(administratorList.size() - 1);
        assertThat(testAdministrator.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testAdministrator.getYearSession()).isEqualTo(DEFAULT_YEAR_SESSION);
        assertThat(testAdministrator.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testAdministrator.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createAdministratorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = administratorRepository.findAll().size();

        // Create the Administrator with an existing ID
        administrator.setId(1L);
        AdministratorDTO administratorDTO = administratorMapper.toDto(administrator);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdministratorMockMvc.perform(post("/api/administrators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(administratorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Administrator in the database
        List<Administrator> administratorList = administratorRepository.findAll();
        assertThat(administratorList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void getAdministratorsByIdFiltering() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        Long id = administrator.getId();

        defaultAdministratorShouldBeFound("id.equals=" + id);
        defaultAdministratorShouldNotBeFound("id.notEquals=" + id);

        defaultAdministratorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAdministratorShouldNotBeFound("id.greaterThan=" + id);

        defaultAdministratorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAdministratorShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAdministratorsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where userId equals to DEFAULT_USER_ID
        defaultAdministratorShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the administratorList where userId equals to UPDATED_USER_ID
        defaultAdministratorShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllAdministratorsByUserIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where userId not equals to DEFAULT_USER_ID
        defaultAdministratorShouldNotBeFound("userId.notEquals=" + DEFAULT_USER_ID);

        // Get all the administratorList where userId not equals to UPDATED_USER_ID
        defaultAdministratorShouldBeFound("userId.notEquals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllAdministratorsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultAdministratorShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the administratorList where userId equals to UPDATED_USER_ID
        defaultAdministratorShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllAdministratorsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where userId is not null
        defaultAdministratorShouldBeFound("userId.specified=true");

        // Get all the administratorList where userId is null
        defaultAdministratorShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    void getAllAdministratorsByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where userId is greater than or equal to DEFAULT_USER_ID
        defaultAdministratorShouldBeFound("userId.greaterThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the administratorList where userId is greater than or equal to UPDATED_USER_ID
        defaultAdministratorShouldNotBeFound("userId.greaterThanOrEqual=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllAdministratorsByUserIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where userId is less than or equal to DEFAULT_USER_ID
        defaultAdministratorShouldBeFound("userId.lessThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the administratorList where userId is less than or equal to SMALLER_USER_ID
        defaultAdministratorShouldNotBeFound("userId.lessThanOrEqual=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllAdministratorsByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where userId is less than DEFAULT_USER_ID
        defaultAdministratorShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the administratorList where userId is less than UPDATED_USER_ID
        defaultAdministratorShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllAdministratorsByUserIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where userId is greater than DEFAULT_USER_ID
        defaultAdministratorShouldNotBeFound("userId.greaterThan=" + DEFAULT_USER_ID);

        // Get all the administratorList where userId is greater than SMALLER_USER_ID
        defaultAdministratorShouldBeFound("userId.greaterThan=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllAdministratorsByYearSessionIsEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where yearSession equals to DEFAULT_YEAR_SESSION
        defaultAdministratorShouldBeFound("yearSession.equals=" + DEFAULT_YEAR_SESSION);

        // Get all the administratorList where yearSession equals to UPDATED_YEAR_SESSION
        defaultAdministratorShouldNotBeFound("yearSession.equals=" + UPDATED_YEAR_SESSION);
    }

    @Test
    @Transactional
    void getAllAdministratorsByYearSessionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where yearSession not equals to DEFAULT_YEAR_SESSION
        defaultAdministratorShouldNotBeFound("yearSession.notEquals=" + DEFAULT_YEAR_SESSION);

        // Get all the administratorList where yearSession not equals to UPDATED_YEAR_SESSION
        defaultAdministratorShouldBeFound("yearSession.notEquals=" + UPDATED_YEAR_SESSION);
    }

    @Test
    @Transactional
    void getAllAdministratorsByYearSessionIsInShouldWork() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where yearSession in DEFAULT_YEAR_SESSION or UPDATED_YEAR_SESSION
        defaultAdministratorShouldBeFound("yearSession.in=" + DEFAULT_YEAR_SESSION + "," + UPDATED_YEAR_SESSION);

        // Get all the administratorList where yearSession equals to UPDATED_YEAR_SESSION
        defaultAdministratorShouldNotBeFound("yearSession.in=" + UPDATED_YEAR_SESSION);
    }

    @Test
    @Transactional
    void getAllAdministratorsByYearSessionIsNullOrNotNull() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where yearSession is not null
        defaultAdministratorShouldBeFound("yearSession.specified=true");

        // Get all the administratorList where yearSession is null
        defaultAdministratorShouldNotBeFound("yearSession.specified=false");
    }

    @Test
    @Transactional
    void getAllAdministratorsByYearSessionContainsSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where yearSession contains DEFAULT_YEAR_SESSION
        defaultAdministratorShouldBeFound("yearSession.contains=" + DEFAULT_YEAR_SESSION);

        // Get all the administratorList where yearSession contains UPDATED_YEAR_SESSION
        defaultAdministratorShouldNotBeFound("yearSession.contains=" + UPDATED_YEAR_SESSION);
    }

    @Test
    @Transactional
    void getAllAdministratorsByYearSessionNotContainsSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where yearSession does not contain DEFAULT_YEAR_SESSION
        defaultAdministratorShouldNotBeFound("yearSession.doesNotContain=" + DEFAULT_YEAR_SESSION);

        // Get all the administratorList where yearSession does not contain UPDATED_YEAR_SESSION
        defaultAdministratorShouldBeFound("yearSession.doesNotContain=" + UPDATED_YEAR_SESSION);
    }

    @Test
    @Transactional
    void getAllAdministratorsByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where role equals to DEFAULT_ROLE
        defaultAdministratorShouldBeFound("role.equals=" + DEFAULT_ROLE);

        // Get all the administratorList where role equals to UPDATED_ROLE
        defaultAdministratorShouldNotBeFound("role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllAdministratorsByRoleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where role not equals to DEFAULT_ROLE
        defaultAdministratorShouldNotBeFound("role.notEquals=" + DEFAULT_ROLE);

        // Get all the administratorList where role not equals to UPDATED_ROLE
        defaultAdministratorShouldBeFound("role.notEquals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllAdministratorsByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where role in DEFAULT_ROLE or UPDATED_ROLE
        defaultAdministratorShouldBeFound("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE);

        // Get all the administratorList where role equals to UPDATED_ROLE
        defaultAdministratorShouldNotBeFound("role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllAdministratorsByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where role is not null
        defaultAdministratorShouldBeFound("role.specified=true");

        // Get all the administratorList where role is null
        defaultAdministratorShouldNotBeFound("role.specified=false");
    }

    @Test
    @Transactional
    void getAllAdministratorsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where status equals to DEFAULT_STATUS
        defaultAdministratorShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the administratorList where status equals to UPDATED_STATUS
        defaultAdministratorShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAdministratorsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where status not equals to DEFAULT_STATUS
        defaultAdministratorShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the administratorList where status not equals to UPDATED_STATUS
        defaultAdministratorShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAdministratorsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultAdministratorShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the administratorList where status equals to UPDATED_STATUS
        defaultAdministratorShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAdministratorsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get all the administratorList where status is not null
        defaultAdministratorShouldBeFound("status.specified=true");

        // Get all the administratorList where status is null
        defaultAdministratorShouldNotBeFound("status.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAdministratorShouldBeFound(String filter) throws Exception {
        restAdministratorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(administrator.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].yearSession").value(hasItem(DEFAULT_YEAR_SESSION)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restAdministratorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAdministratorShouldNotBeFound(String filter) throws Exception {
        restAdministratorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAdministratorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getAdministrator() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        // Get the administrator
        restAdministratorMockMvc.perform(get("/api/administrators/{id}", administrator.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(administrator.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.yearSession").value(DEFAULT_YEAR_SESSION.toString()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAdministrator() throws Exception {
        // Get the administrator
        restAdministratorMockMvc.perform(get("/api/administrators/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAdministrator() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        int databaseSizeBeforeUpdate = administratorRepository.findAll().size();

        // Update the administrator
        Administrator updatedAdministrator = administratorRepository.findById(administrator.getId()).get();
        // Disconnect from session so that the updates on updatedAdministrator are not directly saved in db
        em.detach(updatedAdministrator);
        updatedAdministrator
            .userId(UPDATED_USER_ID)
            .yearSession(UPDATED_YEAR_SESSION)
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS);
        AdministratorDTO administratorDTO = administratorMapper.toDto(updatedAdministrator);

        restAdministratorMockMvc.perform(put("/api/administrators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(administratorDTO)))
            .andExpect(status().isOk());

        // Validate the Administrator in the database
        List<Administrator> administratorList = administratorRepository.findAll();
        assertThat(administratorList).hasSize(databaseSizeBeforeUpdate);
        Administrator testAdministrator = administratorList.get(administratorList.size() - 1);
        assertThat(testAdministrator.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testAdministrator.getYearSession()).isEqualTo(UPDATED_YEAR_SESSION);
        assertThat(testAdministrator.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testAdministrator.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingAdministrator() throws Exception {
        int databaseSizeBeforeUpdate = administratorRepository.findAll().size();

        // Create the Administrator
        AdministratorDTO administratorDTO = administratorMapper.toDto(administrator);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdministratorMockMvc.perform(put("/api/administrators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(administratorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Administrator in the database
        List<Administrator> administratorList = administratorRepository.findAll();
        assertThat(administratorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAdministrator() throws Exception {
        // Initialize the database
        administratorRepository.saveAndFlush(administrator);

        int databaseSizeBeforeDelete = administratorRepository.findAll().size();

        // Delete the administrator
        restAdministratorMockMvc.perform(delete("/api/administrators/{id}", administrator.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Administrator> administratorList = administratorRepository.findAll();
        assertThat(administratorList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Administrator.class);
        Administrator administrator1 = new Administrator();
        administrator1.setId(1L);
        Administrator administrator2 = new Administrator();
        administrator2.setId(administrator1.getId());
        assertThat(administrator1).isEqualTo(administrator2);
        administrator2.setId(2L);
        assertThat(administrator1).isNotEqualTo(administrator2);
        administrator1.setId(null);
        assertThat(administrator1).isNotEqualTo(administrator2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AdministratorDTO.class);
        AdministratorDTO administratorDTO1 = new AdministratorDTO();
        administratorDTO1.setId(1L);
        AdministratorDTO administratorDTO2 = new AdministratorDTO();
        assertThat(administratorDTO1).isNotEqualTo(administratorDTO2);
        administratorDTO2.setId(administratorDTO1.getId());
        assertThat(administratorDTO1).isEqualTo(administratorDTO2);
        administratorDTO2.setId(2L);
        assertThat(administratorDTO1).isNotEqualTo(administratorDTO2);
        administratorDTO1.setId(null);
        assertThat(administratorDTO1).isNotEqualTo(administratorDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(administratorMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(administratorMapper.fromId(null)).isNull();
    }
}
