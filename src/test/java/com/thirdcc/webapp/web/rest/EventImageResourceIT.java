package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.EventImage;
import com.thirdcc.webapp.repository.EventImageRepository;
import com.thirdcc.webapp.service.EventImageService;
import com.thirdcc.webapp.service.dto.EventImageDTO;
import com.thirdcc.webapp.service.mapper.EventImageMapper;

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
 * Integration tests for the {@Link EventImageResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class EventImageResourceIT {

    private static final String ENTITY_API_URL = "/api/event-images";

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long SMALLER_EVENT_ID = DEFAULT_EVENT_ID - 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final Long DEFAULT_IMAGE_STORAGE_ID = 1L;
    private static final Long SMALLER_IMAGE_STORAGE_ID = DEFAULT_IMAGE_STORAGE_ID - 1L;
    private static final Long UPDATED_IMAGE_STORAGE_ID = 2L;

    @Autowired
    private EventImageRepository eventImageRepository;

    @Autowired
    private EventImageMapper eventImageMapper;

    @Autowired
    private EventImageService eventImageService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventImageMockMvc;

    private EventImage eventImage;

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
    public static EventImage createEntity(EntityManager em) {
        EventImage eventImage = new EventImage()
            .eventId(DEFAULT_EVENT_ID)
            .imageStorageId(DEFAULT_IMAGE_STORAGE_ID);
        return eventImage;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventImage createUpdatedEntity(EntityManager em) {
        EventImage eventImage = new EventImage()
            .eventId(UPDATED_EVENT_ID)
            .imageStorageId(UPDATED_IMAGE_STORAGE_ID);
        return eventImage;
    }

    @BeforeEach
    public void initTest() {
        eventImage = createEntity(em);
    }

    @Test
    @Transactional
    public void createEventImage() throws Exception {
        int databaseSizeBeforeCreate = eventImageRepository.findAll().size();

        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);
        restEventImageMockMvc.perform(post("/api/event-images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventImageDTO)))
            .andExpect(status().isCreated());

        // Validate the EventImage in the database
        List<EventImage> eventImageList = eventImageRepository.findAll();
        assertThat(eventImageList).hasSize(databaseSizeBeforeCreate + 1);
        EventImage testEventImage = eventImageList.get(eventImageList.size() - 1);
        assertThat(testEventImage.getEventId()).isEqualTo(DEFAULT_EVENT_ID);
        assertThat(testEventImage.getImageStorageId()).isEqualTo(DEFAULT_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    public void createEventImageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventImageRepository.findAll().size();

        // Create the EventImage with an existing ID
        eventImage.setId(1L);
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventImageMockMvc.perform(post("/api/event-images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventImage in the database
        List<EventImage> eventImageList = eventImageRepository.findAll();
        assertThat(eventImageList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllEventImages() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList
        restEventImageMockMvc.perform(get("/api/event-images?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].imageStorageId").value(hasItem(DEFAULT_IMAGE_STORAGE_ID.intValue())));
    }

    @Test
    @Transactional
    void getEventImagesByIdFiltering() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        Long id = eventImage.getId();

        defaultEventImageShouldBeFound("id.equals=" + id);
        defaultEventImageShouldNotBeFound("id.notEquals=" + id);

        defaultEventImageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEventImageShouldNotBeFound("id.greaterThan=" + id);

        defaultEventImageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEventImageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIdIsEqualToSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where eventId equals to DEFAULT_EVENT_ID
        defaultEventImageShouldBeFound("eventId.equals=" + DEFAULT_EVENT_ID);

        // Get all the eventImageList where eventId equals to UPDATED_EVENT_ID
        defaultEventImageShouldNotBeFound("eventId.equals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where eventId not equals to DEFAULT_EVENT_ID
        defaultEventImageShouldNotBeFound("eventId.notEquals=" + DEFAULT_EVENT_ID);

        // Get all the eventImageList where eventId not equals to UPDATED_EVENT_ID
        defaultEventImageShouldBeFound("eventId.notEquals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIdIsInShouldWork() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where eventId in DEFAULT_EVENT_ID or UPDATED_EVENT_ID
        defaultEventImageShouldBeFound("eventId.in=" + DEFAULT_EVENT_ID + "," + UPDATED_EVENT_ID);

        // Get all the eventImageList where eventId equals to UPDATED_EVENT_ID
        defaultEventImageShouldNotBeFound("eventId.in=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where eventId is not null
        defaultEventImageShouldBeFound("eventId.specified=true");

        // Get all the eventImageList where eventId is null
        defaultEventImageShouldNotBeFound("eventId.specified=false");
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where eventId is greater than or equal to DEFAULT_EVENT_ID
        defaultEventImageShouldBeFound("eventId.greaterThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the eventImageList where eventId is greater than or equal to UPDATED_EVENT_ID
        defaultEventImageShouldNotBeFound("eventId.greaterThanOrEqual=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where eventId is less than or equal to DEFAULT_EVENT_ID
        defaultEventImageShouldBeFound("eventId.lessThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the eventImageList where eventId is less than or equal to SMALLER_EVENT_ID
        defaultEventImageShouldNotBeFound("eventId.lessThanOrEqual=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIdIsLessThanSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where eventId is less than DEFAULT_EVENT_ID
        defaultEventImageShouldNotBeFound("eventId.lessThan=" + DEFAULT_EVENT_ID);

        // Get all the eventImageList where eventId is less than UPDATED_EVENT_ID
        defaultEventImageShouldBeFound("eventId.lessThan=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where eventId is greater than DEFAULT_EVENT_ID
        defaultEventImageShouldNotBeFound("eventId.greaterThan=" + DEFAULT_EVENT_ID);

        // Get all the eventImageList where eventId is greater than SMALLER_EVENT_ID
        defaultEventImageShouldBeFound("eventId.greaterThan=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageStorageIdIsEqualToSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageStorageId equals to DEFAULT_IMAGE_STORAGE_ID
        defaultEventImageShouldBeFound("imageStorageId.equals=" + DEFAULT_IMAGE_STORAGE_ID);

        // Get all the eventImageList where imageStorageId equals to UPDATED_IMAGE_STORAGE_ID
        defaultEventImageShouldNotBeFound("imageStorageId.equals=" + UPDATED_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageStorageIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageStorageId not equals to DEFAULT_IMAGE_STORAGE_ID
        defaultEventImageShouldNotBeFound("imageStorageId.notEquals=" + DEFAULT_IMAGE_STORAGE_ID);

        // Get all the eventImageList where imageStorageId not equals to UPDATED_IMAGE_STORAGE_ID
        defaultEventImageShouldBeFound("imageStorageId.notEquals=" + UPDATED_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageStorageIdIsInShouldWork() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageStorageId in DEFAULT_IMAGE_STORAGE_ID or UPDATED_IMAGE_STORAGE_ID
        defaultEventImageShouldBeFound("imageStorageId.in=" + DEFAULT_IMAGE_STORAGE_ID + "," + UPDATED_IMAGE_STORAGE_ID);

        // Get all the eventImageList where imageStorageId equals to UPDATED_IMAGE_STORAGE_ID
        defaultEventImageShouldNotBeFound("imageStorageId.in=" + UPDATED_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageStorageIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageStorageId is not null
        defaultEventImageShouldBeFound("imageStorageId.specified=true");

        // Get all the eventImageList where imageStorageId is null
        defaultEventImageShouldNotBeFound("imageStorageId.specified=false");
    }

    @Test
    @Transactional
    void getAllEventImagesByImageStorageIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageStorageId is greater than or equal to DEFAULT_IMAGE_STORAGE_ID
        defaultEventImageShouldBeFound("imageStorageId.greaterThanOrEqual=" + DEFAULT_IMAGE_STORAGE_ID);

        // Get all the eventImageList where imageStorageId is greater than or equal to UPDATED_IMAGE_STORAGE_ID
        defaultEventImageShouldNotBeFound("imageStorageId.greaterThanOrEqual=" + UPDATED_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageStorageIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageStorageId is less than or equal to DEFAULT_IMAGE_STORAGE_ID
        defaultEventImageShouldBeFound("imageStorageId.lessThanOrEqual=" + DEFAULT_IMAGE_STORAGE_ID);

        // Get all the eventImageList where imageStorageId is less than or equal to SMALLER_IMAGE_STORAGE_ID
        defaultEventImageShouldNotBeFound("imageStorageId.lessThanOrEqual=" + SMALLER_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageStorageIdIsLessThanSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageStorageId is less than DEFAULT_IMAGE_STORAGE_ID
        defaultEventImageShouldNotBeFound("imageStorageId.lessThan=" + DEFAULT_IMAGE_STORAGE_ID);

        // Get all the eventImageList where imageStorageId is less than UPDATED_IMAGE_STORAGE_ID
        defaultEventImageShouldBeFound("imageStorageId.lessThan=" + UPDATED_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageStorageIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageStorageId is greater than DEFAULT_IMAGE_STORAGE_ID
        defaultEventImageShouldNotBeFound("imageStorageId.greaterThan=" + DEFAULT_IMAGE_STORAGE_ID);

        // Get all the eventImageList where imageStorageId is greater than SMALLER_IMAGE_STORAGE_ID
        defaultEventImageShouldBeFound("imageStorageId.greaterThan=" + SMALLER_IMAGE_STORAGE_ID);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventImageShouldBeFound(String filter) throws Exception {
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].imageStorageId").value(hasItem(DEFAULT_IMAGE_STORAGE_ID.intValue())));

        // Check, that the count call also returns 1
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventImageShouldNotBeFound(String filter) throws Exception {
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getEventImage() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        // Get the eventImage
        restEventImageMockMvc.perform(get("/api/event-images/{id}", eventImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(eventImage.getId().intValue()))
            .andExpect(jsonPath("$.eventId").value(DEFAULT_EVENT_ID.intValue()))
            .andExpect(jsonPath("$.imageStorageId").value(DEFAULT_IMAGE_STORAGE_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingEventImage() throws Exception {
        // Get the eventImage
        restEventImageMockMvc.perform(get("/api/event-images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEventImage() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        int databaseSizeBeforeUpdate = eventImageRepository.findAll().size();

        // Update the eventImage
        EventImage updatedEventImage = eventImageRepository.findById(eventImage.getId()).get();
        // Disconnect from session so that the updates on updatedEventImage are not directly saved in db
        em.detach(updatedEventImage);
        updatedEventImage
            .eventId(UPDATED_EVENT_ID)
            .imageStorageId(UPDATED_IMAGE_STORAGE_ID);
        EventImageDTO eventImageDTO = eventImageMapper.toDto(updatedEventImage);

        restEventImageMockMvc.perform(put("/api/event-images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventImageDTO)))
            .andExpect(status().isOk());

        // Validate the EventImage in the database
        List<EventImage> eventImageList = eventImageRepository.findAll();
        assertThat(eventImageList).hasSize(databaseSizeBeforeUpdate);
        EventImage testEventImage = eventImageList.get(eventImageList.size() - 1);
        assertThat(testEventImage.getEventId()).isEqualTo(UPDATED_EVENT_ID);
        assertThat(testEventImage.getImageStorageId()).isEqualTo(UPDATED_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingEventImage() throws Exception {
        int databaseSizeBeforeUpdate = eventImageRepository.findAll().size();

        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventImageMockMvc.perform(put("/api/event-images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventImage in the database
        List<EventImage> eventImageList = eventImageRepository.findAll();
        assertThat(eventImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEventImage() throws Exception {
        // Initialize the database
        eventImageRepository.saveAndFlush(eventImage);

        int databaseSizeBeforeDelete = eventImageRepository.findAll().size();

        // Delete the eventImage
        restEventImageMockMvc.perform(delete("/api/event-images/{id}", eventImage.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventImage> eventImageList = eventImageRepository.findAll();
        assertThat(eventImageList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventImage.class);
        EventImage eventImage1 = new EventImage();
        eventImage1.setId(1L);
        EventImage eventImage2 = new EventImage();
        eventImage2.setId(eventImage1.getId());
        assertThat(eventImage1).isEqualTo(eventImage2);
        eventImage2.setId(2L);
        assertThat(eventImage1).isNotEqualTo(eventImage2);
        eventImage1.setId(null);
        assertThat(eventImage1).isNotEqualTo(eventImage2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventImageDTO.class);
        EventImageDTO eventImageDTO1 = new EventImageDTO();
        eventImageDTO1.setId(1L);
        EventImageDTO eventImageDTO2 = new EventImageDTO();
        assertThat(eventImageDTO1).isNotEqualTo(eventImageDTO2);
        eventImageDTO2.setId(eventImageDTO1.getId());
        assertThat(eventImageDTO1).isEqualTo(eventImageDTO2);
        eventImageDTO2.setId(2L);
        assertThat(eventImageDTO1).isNotEqualTo(eventImageDTO2);
        eventImageDTO1.setId(null);
        assertThat(eventImageDTO1).isNotEqualTo(eventImageDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(eventImageMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventImageMapper.fromId(null)).isNull();
    }
}
