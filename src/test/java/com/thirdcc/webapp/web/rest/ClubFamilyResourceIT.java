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

    // from club_family_TestFaker.csv
    private static final Long FIXED_CLUB_FAMILY_ID = 2L;

    private static final String DEFAULT_NAME = "Jin Long";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLOGAN = "金龙精神, 靓仔美人";
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
    public static ClubFamily createEntity() {
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
        clubFamily = createEntity();
    }

    @Test
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

        clubFamilyRepository.deleteById(testClubFamily.getId());
    }

    @Test
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
    void getAllClubFamilies() throws Exception {
        // Initialize the databaseFIXED_CLUB_FAMILY_ID

        // Get all the clubFamilyList
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(FIXED_CLUB_FAMILY_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN.toString())));
    }

    @Test
    void getClubFamiliesByIdFiltering() throws Exception {
        // Initialize the databaseFIXED_CLUB_FAMILY_ID

        Long id = FIXED_CLUB_FAMILY_ID;

        defaultClubFamilyShouldBeFound("id.equals=" + id);
    }

    @Test
    void getAllClubFamiliesByNameIsEqualToSomething() throws Exception {
        // Initialize the database

        // Get all the clubFamilyList where name equals to DEFAULT_NAME
        defaultClubFamilyShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the clubFamilyList where name equals to UPDATED_NAME
        defaultClubFamilyShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllClubFamiliesByNameIsInShouldWork() throws Exception {
        // Initialize the database

        // Get all the clubFamilyList where name in DEFAULT_NAME or UPDATED_NAME
        defaultClubFamilyShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the clubFamilyList where name equals to UPDATED_NAME
        defaultClubFamilyShouldNotBeFound("name.in=" + UPDATED_NAME);
    }


    @Test
    void getAllClubFamiliesByNameContainsSomething() throws Exception {
        // Initialize the database

        // Get all the clubFamilyList where name contains DEFAULT_NAME
        defaultClubFamilyShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the clubFamilyList where name contains UPDATED_NAME
        defaultClubFamilyShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }


    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClubFamilyShouldBeFound(String filter) throws Exception {
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(FIXED_CLUB_FAMILY_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN.toString())));

        // Check, that the count call also returns 1
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClubFamilyShouldNotBeFound(String filter) throws Exception {
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClubFamilyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("0"));
    }

    @Test
    public void getClubFamily() throws Exception {
        // Initialize the database

        // Get the clubFamily
        restClubFamilyMockMvc.perform(get("/api/club-families/{id}", FIXED_CLUB_FAMILY_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(FIXED_CLUB_FAMILY_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.slogan").value(DEFAULT_SLOGAN.toString()));
    }

    @Test
    public void getNonExistingClubFamily() throws Exception {
        // Get the clubFamily
        restClubFamilyMockMvc.perform(get("/api/club-families/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClubFamily() throws Exception {
        // Initialize the database

        ClubFamily savedClubFamily = clubFamilyRepository.saveAndFlush(createEntity());
        int databaseSizeBeforeUpdate = clubFamilyRepository.findAll().size();

        ClubFamilyDTO clubFamilyDTO = clubFamilyMapper.toDto(savedClubFamily);
        clubFamilyDTO.setName(UPDATED_NAME);
        clubFamilyDTO.setSlogan(UPDATED_SLOGAN);

        restClubFamilyMockMvc.perform(put("/api/club-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clubFamilyDTO)))
            .andExpect(status().isOk());

        // Validate the ClubFamily in the database
        List<ClubFamily> clubFamilyList = clubFamilyRepository.findAll();
        assertThat(clubFamilyList).hasSize(databaseSizeBeforeUpdate);
        ClubFamily testClubFamily =  clubFamilyList.get(clubFamilyList.size() - 1);
        assertThat(testClubFamily.getId()).isEqualTo(savedClubFamily.getId());
        assertThat(testClubFamily.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClubFamily.getSlogan()).isEqualTo(UPDATED_SLOGAN);

        clubFamilyRepository.deleteById(testClubFamily.getId());
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
    public void deleteClubFamily() throws Exception {
        // Initialize the database
        ClubFamily savedClubFamily = clubFamilyRepository.saveAndFlush(createEntity());
        int databaseSizeBeforeDelete = clubFamilyRepository.findAll().size();

        // Delete the clubFamily
        restClubFamilyMockMvc.perform(delete("/api/club-families/{id}", savedClubFamily.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClubFamily> clubFamilyList = clubFamilyRepository.findAll();
        assertThat(clubFamilyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
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
    public void testEntityFromId() {
        assertThat(clubFamilyMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(clubFamilyMapper.fromId(null)).isNull();
    }
}
