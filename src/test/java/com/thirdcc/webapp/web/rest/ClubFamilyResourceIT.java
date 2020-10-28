package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.ClubFamily;
import com.thirdcc.webapp.repository.ClubFamilyRepository;
import com.thirdcc.webapp.service.ClubFamilyService;
import com.thirdcc.webapp.service.dto.ClubFamilyDTO;
import com.thirdcc.webapp.service.mapper.ClubFamilyMapper;
import com.thirdcc.webapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.thirdcc.webapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link ClubFamilyResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
public class ClubFamilyResourceIT {

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
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restClubFamilyMockMvc;

    private ClubFamily clubFamily;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClubFamilyResource clubFamilyResource = new ClubFamilyResource(clubFamilyService);
        this.restClubFamilyMockMvc = MockMvcBuilders.standaloneSetup(clubFamilyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
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
    public void getAllClubFamilies() throws Exception {
        // Initialize the database
        clubFamilyRepository.saveAndFlush(clubFamily);

        // Get all the clubFamilyList
        restClubFamilyMockMvc.perform(get("/api/club-families?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clubFamily.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN.toString())));
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
