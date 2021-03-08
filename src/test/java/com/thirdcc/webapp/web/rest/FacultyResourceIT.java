package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Faculty;
import com.thirdcc.webapp.repository.FacultyRepository;
import com.thirdcc.webapp.service.FacultyService;

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
 * Integration tests for the {@Link FacultyResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class FacultyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacultyMockMvc;

    private Faculty faculty;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FacultyResource facultyResource = new FacultyResource(facultyService);
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Faculty createEntity(EntityManager em) {
        Faculty faculty = new Faculty()
            .name(DEFAULT_NAME)
            .shortName(DEFAULT_SHORT_NAME);
        return faculty;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Faculty createUpdatedEntity(EntityManager em) {
        Faculty faculty = new Faculty()
            .name(UPDATED_NAME)
            .shortName(UPDATED_SHORT_NAME);
        return faculty;
    }

    @BeforeEach
    public void initTest() {
        faculty = createEntity(em);
    }

    @Test
    @Transactional
    public void createFaculty() throws Exception {
        int databaseSizeBeforeCreate = facultyRepository.findAll().size();

        // Create the Faculty
        restFacultyMockMvc.perform(post("/api/faculties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(faculty)))
            .andExpect(status().isCreated());

        // Validate the Faculty in the database
        List<Faculty> facultyList = facultyRepository.findAll();
        assertThat(facultyList).hasSize(databaseSizeBeforeCreate + 1);
        Faculty testFaculty = facultyList.get(facultyList.size() - 1);
        assertThat(testFaculty.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFaculty.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
    }

    @Test
    @Transactional
    public void createFacultyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = facultyRepository.findAll().size();

        // Create the Faculty with an existing ID
        faculty.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacultyMockMvc.perform(post("/api/faculties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(faculty)))
            .andExpect(status().isBadRequest());

        // Validate the Faculty in the database
        List<Faculty> facultyList = facultyRepository.findAll();
        assertThat(facultyList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllFaculties() throws Exception {
        // Initialize the database
        facultyRepository.saveAndFlush(faculty);

        // Get all the facultyList
        restFacultyMockMvc.perform(get("/api/faculties?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(faculty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getFaculty() throws Exception {
        // Initialize the database
        facultyRepository.saveAndFlush(faculty);

        // Get the faculty
        restFacultyMockMvc.perform(get("/api/faculties/{id}", faculty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(faculty.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFaculty() throws Exception {
        // Get the faculty
        restFacultyMockMvc.perform(get("/api/faculties/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFaculty() throws Exception {
        // Initialize the database
        facultyService.save(faculty);

        int databaseSizeBeforeUpdate = facultyRepository.findAll().size();

        // Update the faculty
        Faculty updatedFaculty = facultyRepository.findById(faculty.getId()).get();
        // Disconnect from session so that the updates on updatedFaculty are not directly saved in db
        em.detach(updatedFaculty);
        updatedFaculty
            .name(UPDATED_NAME)
            .shortName(UPDATED_SHORT_NAME);

        restFacultyMockMvc.perform(put("/api/faculties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFaculty)))
            .andExpect(status().isOk());

        // Validate the Faculty in the database
        List<Faculty> facultyList = facultyRepository.findAll();
        assertThat(facultyList).hasSize(databaseSizeBeforeUpdate);
        Faculty testFaculty = facultyList.get(facultyList.size() - 1);
        assertThat(testFaculty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFaculty.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingFaculty() throws Exception {
        int databaseSizeBeforeUpdate = facultyRepository.findAll().size();

        // Create the Faculty

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacultyMockMvc.perform(put("/api/faculties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(faculty)))
            .andExpect(status().isBadRequest());

        // Validate the Faculty in the database
        List<Faculty> facultyList = facultyRepository.findAll();
        assertThat(facultyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFaculty() throws Exception {
        // Initialize the database
        facultyService.save(faculty);

        int databaseSizeBeforeDelete = facultyRepository.findAll().size();

        // Delete the faculty
        restFacultyMockMvc.perform(delete("/api/faculties/{id}", faculty.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Faculty> facultyList = facultyRepository.findAll();
        assertThat(facultyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Faculty.class);
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        Faculty faculty2 = new Faculty();
        faculty2.setId(faculty1.getId());
        assertThat(faculty1).isEqualTo(faculty2);
        faculty2.setId(2L);
        assertThat(faculty1).isNotEqualTo(faculty2);
        faculty1.setId(null);
        assertThat(faculty1).isNotEqualTo(faculty2);
    }
}
