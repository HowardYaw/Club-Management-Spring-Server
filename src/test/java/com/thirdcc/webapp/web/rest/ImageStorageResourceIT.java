package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.ImageStorage;
import com.thirdcc.webapp.repository.ImageStorageRepository;
import com.thirdcc.webapp.service.ImageStorageService;
import com.thirdcc.webapp.service.dto.ImageStorageDTO;
import com.thirdcc.webapp.service.mapper.ImageStorageMapper;

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
 * Integration tests for the {@Link ImageStorageResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class ImageStorageResourceIT {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    @Autowired
    private ImageStorageRepository imageStorageRepository;

    @Autowired
    private ImageStorageMapper imageStorageMapper;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImageStorageMockMvc;

    private ImageStorage imageStorage;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImageStorage createEntity(EntityManager em) {
        ImageStorage imageStorage = new ImageStorage()
            .imageUrl(DEFAULT_IMAGE_URL)
            .fileName(DEFAULT_FILE_NAME)
            .fileType(DEFAULT_FILE_TYPE);
        return imageStorage;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImageStorage createUpdatedEntity(EntityManager em) {
        ImageStorage imageStorage = new ImageStorage()
            .imageUrl(UPDATED_IMAGE_URL)
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE);
        return imageStorage;
    }

    @BeforeEach
    public void initTest() {
        imageStorage = createEntity(em);
    }

//    @Test
    @Transactional
    public void getAllImageStorages() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList
        restImageStorageMockMvc.perform(get("/api/image-storages?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imageStorage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL.toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME.toString())))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE.toString())));
    }

//    @Test
    @Transactional
    public void getImageStorage() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get the imageStorage
        restImageStorageMockMvc.perform(get("/api/image-storages/{id}", imageStorage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(imageStorage.getId().intValue()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL.toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME.toString()))
            .andExpect(jsonPath("$.fileType").value(DEFAULT_FILE_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingImageStorage() throws Exception {
        // Get the imageStorage
        restImageStorageMockMvc.perform(get("/api/image-storages/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

//    @Test
    @Transactional
    public void deleteImageStorage() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        int databaseSizeBeforeDelete = imageStorageRepository.findAll().size();

        // Delete the imageStorage
        restImageStorageMockMvc.perform(delete("/api/image-storages/{id}", imageStorage.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ImageStorage> imageStorageList = imageStorageRepository.findAll();
        assertThat(imageStorageList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImageStorage.class);
        ImageStorage imageStorage1 = new ImageStorage();
        imageStorage1.setId(1L);
        ImageStorage imageStorage2 = new ImageStorage();
        imageStorage2.setId(imageStorage1.getId());
        assertThat(imageStorage1).isEqualTo(imageStorage2);
        imageStorage2.setId(2L);
        assertThat(imageStorage1).isNotEqualTo(imageStorage2);
        imageStorage1.setId(null);
        assertThat(imageStorage1).isNotEqualTo(imageStorage2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImageStorageDTO.class);
        ImageStorageDTO imageStorageDTO1 = new ImageStorageDTO();
        imageStorageDTO1.setId(1L);
        ImageStorageDTO imageStorageDTO2 = new ImageStorageDTO();
        assertThat(imageStorageDTO1).isNotEqualTo(imageStorageDTO2);
        imageStorageDTO2.setId(imageStorageDTO1.getId());
        assertThat(imageStorageDTO1).isEqualTo(imageStorageDTO2);
        imageStorageDTO2.setId(2L);
        assertThat(imageStorageDTO1).isNotEqualTo(imageStorageDTO2);
        imageStorageDTO1.setId(null);
        assertThat(imageStorageDTO1).isNotEqualTo(imageStorageDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(imageStorageMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(imageStorageMapper.fromId(null)).isNull();
    }
}
