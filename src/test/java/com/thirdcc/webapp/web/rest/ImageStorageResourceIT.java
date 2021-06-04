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

    private static final String ENTITY_API_URL = "/api/image-storages";

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

    @Test
    @Transactional
    void getImageStoragesByIdFiltering() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        Long id = imageStorage.getId();

        defaultImageStorageShouldBeFound("id.equals=" + id);
        defaultImageStorageShouldNotBeFound("id.notEquals=" + id);

        defaultImageStorageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultImageStorageShouldNotBeFound("id.greaterThan=" + id);

        defaultImageStorageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultImageStorageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllImageStoragesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where imageUrl equals to DEFAULT_IMAGE_URL
        defaultImageStorageShouldBeFound("imageUrl.equals=" + DEFAULT_IMAGE_URL);

        // Get all the imageStorageList where imageUrl equals to UPDATED_IMAGE_URL
        defaultImageStorageShouldNotBeFound("imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllImageStoragesByImageUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where imageUrl not equals to DEFAULT_IMAGE_URL
        defaultImageStorageShouldNotBeFound("imageUrl.notEquals=" + DEFAULT_IMAGE_URL);

        // Get all the imageStorageList where imageUrl not equals to UPDATED_IMAGE_URL
        defaultImageStorageShouldBeFound("imageUrl.notEquals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllImageStoragesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where imageUrl in DEFAULT_IMAGE_URL or UPDATED_IMAGE_URL
        defaultImageStorageShouldBeFound("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL);

        // Get all the imageStorageList where imageUrl equals to UPDATED_IMAGE_URL
        defaultImageStorageShouldNotBeFound("imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllImageStoragesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where imageUrl is not null
        defaultImageStorageShouldBeFound("imageUrl.specified=true");

        // Get all the imageStorageList where imageUrl is null
        defaultImageStorageShouldNotBeFound("imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllImageStoragesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where imageUrl contains DEFAULT_IMAGE_URL
        defaultImageStorageShouldBeFound("imageUrl.contains=" + DEFAULT_IMAGE_URL);

        // Get all the imageStorageList where imageUrl contains UPDATED_IMAGE_URL
        defaultImageStorageShouldNotBeFound("imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllImageStoragesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where imageUrl does not contain DEFAULT_IMAGE_URL
        defaultImageStorageShouldNotBeFound("imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);

        // Get all the imageStorageList where imageUrl does not contain UPDATED_IMAGE_URL
        defaultImageStorageShouldBeFound("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileName equals to DEFAULT_FILE_NAME
        defaultImageStorageShouldBeFound("fileName.equals=" + DEFAULT_FILE_NAME);

        // Get all the imageStorageList where fileName equals to UPDATED_FILE_NAME
        defaultImageStorageShouldNotBeFound("fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileName not equals to DEFAULT_FILE_NAME
        defaultImageStorageShouldNotBeFound("fileName.notEquals=" + DEFAULT_FILE_NAME);

        // Get all the imageStorageList where fileName not equals to UPDATED_FILE_NAME
        defaultImageStorageShouldBeFound("fileName.notEquals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileName in DEFAULT_FILE_NAME or UPDATED_FILE_NAME
        defaultImageStorageShouldBeFound("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME);

        // Get all the imageStorageList where fileName equals to UPDATED_FILE_NAME
        defaultImageStorageShouldNotBeFound("fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileName is not null
        defaultImageStorageShouldBeFound("fileName.specified=true");

        // Get all the imageStorageList where fileName is null
        defaultImageStorageShouldNotBeFound("fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileNameContainsSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileName contains DEFAULT_FILE_NAME
        defaultImageStorageShouldBeFound("fileName.contains=" + DEFAULT_FILE_NAME);

        // Get all the imageStorageList where fileName contains UPDATED_FILE_NAME
        defaultImageStorageShouldNotBeFound("fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileName does not contain DEFAULT_FILE_NAME
        defaultImageStorageShouldNotBeFound("fileName.doesNotContain=" + DEFAULT_FILE_NAME);

        // Get all the imageStorageList where fileName does not contain UPDATED_FILE_NAME
        defaultImageStorageShouldBeFound("fileName.doesNotContain=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileType equals to DEFAULT_FILE_TYPE
        defaultImageStorageShouldBeFound("fileType.equals=" + DEFAULT_FILE_TYPE);

        // Get all the imageStorageList where fileType equals to UPDATED_FILE_TYPE
        defaultImageStorageShouldNotBeFound("fileType.equals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileType not equals to DEFAULT_FILE_TYPE
        defaultImageStorageShouldNotBeFound("fileType.notEquals=" + DEFAULT_FILE_TYPE);

        // Get all the imageStorageList where fileType not equals to UPDATED_FILE_TYPE
        defaultImageStorageShouldBeFound("fileType.notEquals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileTypeIsInShouldWork() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileType in DEFAULT_FILE_TYPE or UPDATED_FILE_TYPE
        defaultImageStorageShouldBeFound("fileType.in=" + DEFAULT_FILE_TYPE + "," + UPDATED_FILE_TYPE);

        // Get all the imageStorageList where fileType equals to UPDATED_FILE_TYPE
        defaultImageStorageShouldNotBeFound("fileType.in=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileType is not null
        defaultImageStorageShouldBeFound("fileType.specified=true");

        // Get all the imageStorageList where fileType is null
        defaultImageStorageShouldNotBeFound("fileType.specified=false");
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileTypeContainsSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileType contains DEFAULT_FILE_TYPE
        defaultImageStorageShouldBeFound("fileType.contains=" + DEFAULT_FILE_TYPE);

        // Get all the imageStorageList where fileType contains UPDATED_FILE_TYPE
        defaultImageStorageShouldNotBeFound("fileType.contains=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllImageStoragesByFileTypeNotContainsSomething() throws Exception {
        // Initialize the database
        imageStorageRepository.saveAndFlush(imageStorage);

        // Get all the imageStorageList where fileType does not contain DEFAULT_FILE_TYPE
        defaultImageStorageShouldNotBeFound("fileType.doesNotContain=" + DEFAULT_FILE_TYPE);

        // Get all the imageStorageList where fileType does not contain UPDATED_FILE_TYPE
        defaultImageStorageShouldBeFound("fileType.doesNotContain=" + UPDATED_FILE_TYPE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImageStorageShouldBeFound(String filter) throws Exception {
        restImageStorageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imageStorage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)));

        // Check, that the count call also returns 1
        restImageStorageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImageStorageShouldNotBeFound(String filter) throws Exception {
        restImageStorageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImageStorageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("0"));
    }

    @Test
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

    @Test
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
