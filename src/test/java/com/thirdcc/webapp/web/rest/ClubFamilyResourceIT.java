package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.ClubFamily;
import com.thirdcc.webapp.repository.ClubFamilyRepository;
import com.thirdcc.webapp.service.ClubFamilyQueryService;
import com.thirdcc.webapp.service.ClubFamilyService;
import com.thirdcc.webapp.service.dto.ClubFamilyDTO;
import com.thirdcc.webapp.service.mapper.ClubFamilyMapper;

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
 * Integration tests for the {@Link ClubFamilyResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class ClubFamilyResourceIT {

    private static final String ENTITY_API_URL = "/api/club-families";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLOGAN = "AAAAAAAAAA";
    private static final String UPDATED_SLOGAN = "BBBBBBBBBB";

    @Autowired
    private ClubFamilyRepository clubFamilyRepository;

    @Autowired
    private ClubFamilyMapper clubFamilyMapper;

    @Autowired
    private ClubFamilyService clubFamilyService;

    @Autowired
    private ClubFamilyQueryService clubFamilyQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClubFamilyMockMvc;

    private ClubFamily clubFamily;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClubFamilyResource clubFamilyResource = new ClubFamilyResource(clubFamilyService, clubFamilyQueryService);
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClubFamily createEntity(EntityManager em) {
        ClubFamily clubFamily = new ClubFamily()
            .name(DEFAULT_NAME)
            .slogan(DEFAULT_SLOGAN);
        return clubFamily;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClubFamily createUpdatedEntity(EntityManager em) {
        ClubFamily clubFamily = new ClubFamily()
            .name(UPDATED_NAME)
            .slogan(UPDATED_SLOGAN);
        return clubFamily;
    }

    @BeforeEach
    public void initTest() {
        clubFamily = createEntity(em);
    }

    @Test
    @Transactional
    public void createClubFamily() throws Exception {
        int databaseSizeBeforeCreate = clubFamilyRepository.findAll().size();

        // Create the ClubFamily
        ClubFamilyDTO clubFamilyDTO = clubFamilyMapper.toDto(clubFamily);
        restClubFamilyMockMvc.perform(post("/api/club-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clubFamilyDTO)))
            .andExpect(status().isCreated());

        // Validate the ClubFamily in the database
        List<ClubFamily> clubFamilyList = clubFamilyRepository.findAll();
        assertThat(clubFamilyList).hasSize(databaseSizeBeforeCreate + 1);
        ClubFamily testClubFamily = clubFamilyList.get(clubFamilyList.size() - 1);
        assertThat(testClubFamily.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClubFamily.getSlogan()).isEqualTo(DEFAULT_SLOGAN);
    }

    @Test
    @Transactional
    public void createClubFamilyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = clubFamilyRepository.findAll().size();

        // Create the ClubFamily with an existing ID
        clubFamily.setId(1L);
        ClubFamilyDTO clubFamilyDTO = clubFamilyMapper.toDto(clubFamily);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClubFamilyMockMvc.perform(post("/api/club-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clubFamilyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClubFamily in the database
        List<ClubFamily> clubFamilyList = clubFamilyRepository.findAll();
        assertThat(clubFamilyList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void getAllClubFamilies() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get all the clubFamilyList
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clubFamily.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN.toString())));
    }

    @Test
    @Transactional
    void getClubFamiliesByIdFiltering() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        Long id = clubFamily.getId();

        defaultClubFamilyShouldBeFound("id.equals=" + id);
        defaultClubFamilyShouldNotBeFound("id.notEquals=" + id);

        defaultClubFamilyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultClubFamilyShouldNotBeFound("id.greaterThan=" + id);

        defaultClubFamilyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultClubFamilyShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClubFamiliesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get all the clubFamilyList where name equals to DEFAULT_NAME
        defaultClubFamilyShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the clubFamilyList where name equals to UPDATED_NAME
        defaultClubFamilyShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClubFamiliesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get all the clubFamilyList where name not equals to DEFAULT_NAME
        defaultClubFamilyShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the clubFamilyList where name not equals to UPDATED_NAME
        defaultClubFamilyShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClubFamiliesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get all the clubFamilyList where name in DEFAULT_NAME or UPDATED_NAME
        defaultClubFamilyShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the clubFamilyList where name equals to UPDATED_NAME
        defaultClubFamilyShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClubFamiliesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get all the clubFamilyList where name is not null
        defaultClubFamilyShouldBeFound("name.specified=true");

        // Get all the clubFamilyList where name is null
        defaultClubFamilyShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllClubFamiliesByNameContainsSomething() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get all the clubFamilyList where name contains DEFAULT_NAME
        defaultClubFamilyShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the clubFamilyList where name contains UPDATED_NAME
        defaultClubFamilyShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClubFamiliesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get all the clubFamilyList where name does not contain DEFAULT_NAME
        defaultClubFamilyShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the clubFamilyList where name does not contain UPDATED_NAME
        defaultClubFamilyShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClubFamilyShouldBeFound(String filter) throws Exception {
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clubFamily.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN.toString())));

        // Check, that the count call also returns 1
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClubFamilyShouldNotBeFound(String filter) throws Exception {
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getClubFamily() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get the clubFamily
        restClubFamilyMockMvc.perform(get("/api/club-families/{id}", clubFamily.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(clubFamily.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.slogan").value(DEFAULT_SLOGAN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClubFamily() throws Exception {
        // Get the clubFamily
        restClubFamilyMockMvc.perform(get("/api/club-families/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClubFamily() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        int databaseSizeBeforeUpdate = clubFamilyRepository.findAll().size();

        // Update the clubFamily
        ClubFamily updatedClubFamily = clubFamilyRepository.findById(clubFamily.getId()).get();
        // Disconnect from session so that the updates on updatedClubFamily are not directly saved in db
        em.detach(updatedClubFamily);
        updatedClubFamily
            .name(UPDATED_NAME)
            .slogan(UPDATED_SLOGAN);
        ClubFamilyDTO clubFamilyDTO = clubFamilyMapper.toDto(updatedClubFamily);

        restClubFamilyMockMvc.perform(put("/api/club-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clubFamilyDTO)))
            .andExpect(status().isOk());

        // Validate the ClubFamily in the database
        List<ClubFamily> clubFamilyList = clubFamilyRepository.findAll();
        assertThat(clubFamilyList).hasSize(databaseSizeBeforeUpdate);
        ClubFamily testClubFamily = clubFamilyList.get(clubFamilyList.size() - 1);
        assertThat(testClubFamily.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClubFamily.getSlogan()).isEqualTo(UPDATED_SLOGAN);
    }

    @Test
    @Transactional
    public void updateNonExistingClubFamily() throws Exception {
        int databaseSizeBeforeUpdate = clubFamilyRepository.findAll().size();

        // Create the ClubFamily
        ClubFamilyDTO clubFamilyDTO = clubFamilyMapper.toDto(clubFamily);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClubFamilyMockMvc.perform(put("/api/club-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clubFamilyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClubFamily in the database
        List<ClubFamily> clubFamilyList = clubFamilyRepository.findAll();
        assertThat(clubFamilyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteClubFamily() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        int databaseSizeBeforeDelete = clubFamilyRepository.findAll().size();

        // Delete the clubFamily
        restClubFamilyMockMvc.perform(delete("/api/club-families/{id}", clubFamily.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClubFamily> clubFamilyList = clubFamilyRepository.findAll();
        assertThat(clubFamilyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClubFamily.class);
        ClubFamily clubFamily1 = new ClubFamily();
        clubFamily1.setId(1L);
        ClubFamily clubFamily2 = new ClubFamily();
        clubFamily2.setId(clubFamily1.getId());
        assertThat(clubFamily1).isEqualTo(clubFamily2);
        clubFamily2.setId(2L);
        assertThat(clubFamily1).isNotEqualTo(clubFamily2);
        clubFamily1.setId(null);
        assertThat(clubFamily1).isNotEqualTo(clubFamily2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClubFamilyDTO.class);
        ClubFamilyDTO clubFamilyDTO1 = new ClubFamilyDTO();
        clubFamilyDTO1.setId(1L);
        ClubFamilyDTO clubFamilyDTO2 = new ClubFamilyDTO();
        assertThat(clubFamilyDTO1).isNotEqualTo(clubFamilyDTO2);
        clubFamilyDTO2.setId(clubFamilyDTO1.getId());
        assertThat(clubFamilyDTO1).isEqualTo(clubFamilyDTO2);
        clubFamilyDTO2.setId(2L);
        assertThat(clubFamilyDTO1).isNotEqualTo(clubFamilyDTO2);
        clubFamilyDTO1.setId(null);
        assertThat(clubFamilyDTO1).isNotEqualTo(clubFamilyDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(clubFamilyMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(clubFamilyMapper.fromId(null)).isNull();
    }
}
