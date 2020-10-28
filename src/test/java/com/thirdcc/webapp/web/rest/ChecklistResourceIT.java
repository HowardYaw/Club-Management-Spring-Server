package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Checklist;
import com.thirdcc.webapp.repository.ChecklistRepository;
import com.thirdcc.webapp.service.ChecklistService;
import com.thirdcc.webapp.service.dto.ChecklistDTO;
import com.thirdcc.webapp.service.mapper.ChecklistMapper;
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

import com.thirdcc.webapp.domain.enumeration.ChecklistStatus;
import com.thirdcc.webapp.domain.enumeration.ChecklistType;
/**
 * Integration tests for the {@Link ChecklistResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
public class ChecklistResourceIT {

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ChecklistStatus DEFAULT_STATUS = ChecklistStatus.OPEN;
    private static final ChecklistStatus UPDATED_STATUS = ChecklistStatus.IN_PROGRESS;

    private static final ChecklistType DEFAULT_TYPE = ChecklistType.PREPARATION;
    private static final ChecklistType UPDATED_TYPE = ChecklistType.PURCHASE;

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private ChecklistMapper checklistMapper;

    @Autowired
    private ChecklistService checklistService;

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

    private MockMvc restChecklistMockMvc;

    private Checklist checklist;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChecklistResource checklistResource = new ChecklistResource(checklistService);
        this.restChecklistMockMvc = MockMvcBuilders.standaloneSetup(checklistResource)
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
    public static Checklist createEntity(EntityManager em) {
        Checklist checklist = new Checklist()
            .eventId(DEFAULT_EVENT_ID)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .type(DEFAULT_TYPE);
        return checklist;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checklist createUpdatedEntity(EntityManager em) {
        Checklist checklist = new Checklist()
            .eventId(UPDATED_EVENT_ID)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE);
        return checklist;
    }

    @BeforeEach
    public void initTest() {
        checklist = createEntity(em);
    }

    @Test
    @Transactional
    public void createChecklist() throws Exception {
        int databaseSizeBeforeCreate = checklistRepository.findAll().size();

        // Create the Checklist
        ChecklistDTO checklistDTO = checklistMapper.toDto(checklist);
        restChecklistMockMvc.perform(post("/api/checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(checklistDTO)))
            .andExpect(status().isCreated());

        // Validate the Checklist in the database
        List<Checklist> checklistList = checklistRepository.findAll();
        assertThat(checklistList).hasSize(databaseSizeBeforeCreate + 1);
        Checklist testChecklist = checklistList.get(checklistList.size() - 1);
        assertThat(testChecklist.getEventId()).isEqualTo(DEFAULT_EVENT_ID);
        assertThat(testChecklist.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testChecklist.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testChecklist.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testChecklist.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void createChecklistWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = checklistRepository.findAll().size();

        // Create the Checklist with an existing ID
        checklist.setId(1L);
        ChecklistDTO checklistDTO = checklistMapper.toDto(checklist);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChecklistMockMvc.perform(post("/api/checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(checklistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<Checklist> checklistList = checklistRepository.findAll();
        assertThat(checklistList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllChecklists() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);

        // Get all the checklistList
        restChecklistMockMvc.perform(get("/api/checklists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checklist.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getChecklist() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);

        // Get the checklist
        restChecklistMockMvc.perform(get("/api/checklists/{id}", checklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(checklist.getId().intValue()))
            .andExpect(jsonPath("$.eventId").value(DEFAULT_EVENT_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingChecklist() throws Exception {
        // Get the checklist
        restChecklistMockMvc.perform(get("/api/checklists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChecklist() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);

        int databaseSizeBeforeUpdate = checklistRepository.findAll().size();

        // Update the checklist
        Checklist updatedChecklist = checklistRepository.findById(checklist.getId()).get();
        // Disconnect from session so that the updates on updatedChecklist are not directly saved in db
        em.detach(updatedChecklist);
        updatedChecklist
            .eventId(UPDATED_EVENT_ID)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE);
        ChecklistDTO checklistDTO = checklistMapper.toDto(updatedChecklist);

        restChecklistMockMvc.perform(put("/api/checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(checklistDTO)))
            .andExpect(status().isOk());

        // Validate the Checklist in the database
        List<Checklist> checklistList = checklistRepository.findAll();
        assertThat(checklistList).hasSize(databaseSizeBeforeUpdate);
        Checklist testChecklist = checklistList.get(checklistList.size() - 1);
        assertThat(testChecklist.getEventId()).isEqualTo(UPDATED_EVENT_ID);
        assertThat(testChecklist.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testChecklist.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testChecklist.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testChecklist.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingChecklist() throws Exception {
        int databaseSizeBeforeUpdate = checklistRepository.findAll().size();

        // Create the Checklist
        ChecklistDTO checklistDTO = checklistMapper.toDto(checklist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChecklistMockMvc.perform(put("/api/checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(checklistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<Checklist> checklistList = checklistRepository.findAll();
        assertThat(checklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteChecklist() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);

        int databaseSizeBeforeDelete = checklistRepository.findAll().size();

        // Delete the checklist
        restChecklistMockMvc.perform(delete("/api/checklists/{id}", checklist.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Checklist> checklistList = checklistRepository.findAll();
        assertThat(checklistList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Checklist.class);
        Checklist checklist1 = new Checklist();
        checklist1.setId(1L);
        Checklist checklist2 = new Checklist();
        checklist2.setId(checklist1.getId());
        assertThat(checklist1).isEqualTo(checklist2);
        checklist2.setId(2L);
        assertThat(checklist1).isNotEqualTo(checklist2);
        checklist1.setId(null);
        assertThat(checklist1).isNotEqualTo(checklist2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChecklistDTO.class);
        ChecklistDTO checklistDTO1 = new ChecklistDTO();
        checklistDTO1.setId(1L);
        ChecklistDTO checklistDTO2 = new ChecklistDTO();
        assertThat(checklistDTO1).isNotEqualTo(checklistDTO2);
        checklistDTO2.setId(checklistDTO1.getId());
        assertThat(checklistDTO1).isEqualTo(checklistDTO2);
        checklistDTO2.setId(2L);
        assertThat(checklistDTO1).isNotEqualTo(checklistDTO2);
        checklistDTO1.setId(null);
        assertThat(checklistDTO1).isNotEqualTo(checklistDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(checklistMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(checklistMapper.fromId(null)).isNull();
    }
}
