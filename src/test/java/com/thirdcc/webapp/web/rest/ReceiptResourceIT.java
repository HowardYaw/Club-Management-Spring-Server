package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Receipt;
import com.thirdcc.webapp.repository.ReceiptRepository;
import com.thirdcc.webapp.service.ReceiptService;
import com.thirdcc.webapp.service.dto.ReceiptDTO;
import com.thirdcc.webapp.service.mapper.ReceiptMapper;

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
 * Integration tests for the {@Link ReceiptResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class ReceiptResourceIT {

    private static final String ENTITY_API_URL = "/api/receipts";

    private static final String DEFAULT_RECEIPT_URL = "AAAAAAAAAA";
    private static final String UPDATED_RECEIPT_URL = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptMapper receiptMapper;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReceiptMockMvc;

    private Receipt receipt;

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
    public static Receipt createEntity(EntityManager em) {
        Receipt receipt = new Receipt()
            .receiptUrl(DEFAULT_RECEIPT_URL)
            .fileName(DEFAULT_FILE_NAME)
            .fileType(DEFAULT_FILE_TYPE);
        return receipt;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Receipt createUpdatedEntity(EntityManager em) {
        Receipt receipt = new Receipt()
            .receiptUrl(UPDATED_RECEIPT_URL)
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE);
        return receipt;
    }

    @BeforeEach
    public void initTest() {
        receipt = createEntity(em);
    }

    @Test
    @Transactional
    public void createReceipt() throws Exception {
        int databaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Receipt
        ReceiptDTO receiptDTO = receiptMapper.toDto(receipt);
        restReceiptMockMvc.perform(post("/api/receipts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(receiptDTO)))
            .andExpect(status().isCreated());

        // Validate the Receipt in the database
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(databaseSizeBeforeCreate + 1);
        Receipt testReceipt = receiptList.get(receiptList.size() - 1);
        assertThat(testReceipt.getReceiptUrl()).isEqualTo(DEFAULT_RECEIPT_URL);
        assertThat(testReceipt.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testReceipt.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
    }

    @Test
    @Transactional
    public void createReceiptWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Receipt with an existing ID
        receipt.setId(1L);
        ReceiptDTO receiptDTO = receiptMapper.toDto(receipt);

        // An entity with an existing ID cannot be created, so this API call must fail
        restReceiptMockMvc.perform(post("/api/receipts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(receiptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Receipt in the database
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void getReceiptsByIdFiltering() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        Long id = receipt.getId();

        defaultReceiptShouldBeFound("id.equals=" + id);
        defaultReceiptShouldNotBeFound("id.notEquals=" + id);

        defaultReceiptShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultReceiptShouldNotBeFound("id.greaterThan=" + id);

        defaultReceiptShouldBeFound("id.lessThanOrEqual=" + id);
        defaultReceiptShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReceiptsByReceiptUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where receiptUrl equals to DEFAULT_RECEIPT_URL
        defaultReceiptShouldBeFound("receiptUrl.equals=" + DEFAULT_RECEIPT_URL);

        // Get all the receiptList where receiptUrl equals to UPDATED_RECEIPT_URL
        defaultReceiptShouldNotBeFound("receiptUrl.equals=" + UPDATED_RECEIPT_URL);
    }

    @Test
    @Transactional
    void getAllReceiptsByReceiptUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where receiptUrl not equals to DEFAULT_RECEIPT_URL
        defaultReceiptShouldNotBeFound("receiptUrl.notEquals=" + DEFAULT_RECEIPT_URL);

        // Get all the receiptList where receiptUrl not equals to UPDATED_RECEIPT_URL
        defaultReceiptShouldBeFound("receiptUrl.notEquals=" + UPDATED_RECEIPT_URL);
    }

    @Test
    @Transactional
    void getAllReceiptsByReceiptUrlIsInShouldWork() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where receiptUrl in DEFAULT_RECEIPT_URL or UPDATED_RECEIPT_URL
        defaultReceiptShouldBeFound("receiptUrl.in=" + DEFAULT_RECEIPT_URL + "," + UPDATED_RECEIPT_URL);

        // Get all the receiptList where receiptUrl equals to UPDATED_RECEIPT_URL
        defaultReceiptShouldNotBeFound("receiptUrl.in=" + UPDATED_RECEIPT_URL);
    }

    @Test
    @Transactional
    void getAllReceiptsByReceiptUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where receiptUrl is not null
        defaultReceiptShouldBeFound("receiptUrl.specified=true");

        // Get all the receiptList where receiptUrl is null
        defaultReceiptShouldNotBeFound("receiptUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllReceiptsByReceiptUrlContainsSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where receiptUrl contains DEFAULT_RECEIPT_URL
        defaultReceiptShouldBeFound("receiptUrl.contains=" + DEFAULT_RECEIPT_URL);

        // Get all the receiptList where receiptUrl contains UPDATED_RECEIPT_URL
        defaultReceiptShouldNotBeFound("receiptUrl.contains=" + UPDATED_RECEIPT_URL);
    }

    @Test
    @Transactional
    void getAllReceiptsByReceiptUrlNotContainsSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where receiptUrl does not contain DEFAULT_RECEIPT_URL
        defaultReceiptShouldNotBeFound("receiptUrl.doesNotContain=" + DEFAULT_RECEIPT_URL);

        // Get all the receiptList where receiptUrl does not contain UPDATED_RECEIPT_URL
        defaultReceiptShouldBeFound("receiptUrl.doesNotContain=" + UPDATED_RECEIPT_URL);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileName equals to DEFAULT_FILE_NAME
        defaultReceiptShouldBeFound("fileName.equals=" + DEFAULT_FILE_NAME);

        // Get all the receiptList where fileName equals to UPDATED_FILE_NAME
        defaultReceiptShouldNotBeFound("fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileName not equals to DEFAULT_FILE_NAME
        defaultReceiptShouldNotBeFound("fileName.notEquals=" + DEFAULT_FILE_NAME);

        // Get all the receiptList where fileName not equals to UPDATED_FILE_NAME
        defaultReceiptShouldBeFound("fileName.notEquals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileName in DEFAULT_FILE_NAME or UPDATED_FILE_NAME
        defaultReceiptShouldBeFound("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME);

        // Get all the receiptList where fileName equals to UPDATED_FILE_NAME
        defaultReceiptShouldNotBeFound("fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileName is not null
        defaultReceiptShouldBeFound("fileName.specified=true");

        // Get all the receiptList where fileName is null
        defaultReceiptShouldNotBeFound("fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllReceiptsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileName contains DEFAULT_FILE_NAME
        defaultReceiptShouldBeFound("fileName.contains=" + DEFAULT_FILE_NAME);

        // Get all the receiptList where fileName contains UPDATED_FILE_NAME
        defaultReceiptShouldNotBeFound("fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileName does not contain DEFAULT_FILE_NAME
        defaultReceiptShouldNotBeFound("fileName.doesNotContain=" + DEFAULT_FILE_NAME);

        // Get all the receiptList where fileName does not contain UPDATED_FILE_NAME
        defaultReceiptShouldBeFound("fileName.doesNotContain=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileType equals to DEFAULT_FILE_TYPE
        defaultReceiptShouldBeFound("fileType.equals=" + DEFAULT_FILE_TYPE);

        // Get all the receiptList where fileType equals to UPDATED_FILE_TYPE
        defaultReceiptShouldNotBeFound("fileType.equals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileType not equals to DEFAULT_FILE_TYPE
        defaultReceiptShouldNotBeFound("fileType.notEquals=" + DEFAULT_FILE_TYPE);

        // Get all the receiptList where fileType not equals to UPDATED_FILE_TYPE
        defaultReceiptShouldBeFound("fileType.notEquals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileTypeIsInShouldWork() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileType in DEFAULT_FILE_TYPE or UPDATED_FILE_TYPE
        defaultReceiptShouldBeFound("fileType.in=" + DEFAULT_FILE_TYPE + "," + UPDATED_FILE_TYPE);

        // Get all the receiptList where fileType equals to UPDATED_FILE_TYPE
        defaultReceiptShouldNotBeFound("fileType.in=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileType is not null
        defaultReceiptShouldBeFound("fileType.specified=true");

        // Get all the receiptList where fileType is null
        defaultReceiptShouldNotBeFound("fileType.specified=false");
    }

    @Test
    @Transactional
    void getAllReceiptsByFileTypeContainsSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileType contains DEFAULT_FILE_TYPE
        defaultReceiptShouldBeFound("fileType.contains=" + DEFAULT_FILE_TYPE);

        // Get all the receiptList where fileType contains UPDATED_FILE_TYPE
        defaultReceiptShouldNotBeFound("fileType.contains=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllReceiptsByFileTypeNotContainsSomething() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get all the receiptList where fileType does not contain DEFAULT_FILE_TYPE
        defaultReceiptShouldNotBeFound("fileType.doesNotContain=" + DEFAULT_FILE_TYPE);

        // Get all the receiptList where fileType does not contain UPDATED_FILE_TYPE
        defaultReceiptShouldBeFound("fileType.doesNotContain=" + UPDATED_FILE_TYPE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReceiptShouldBeFound(String filter) throws Exception {
        restReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(receipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptUrl").value(hasItem(DEFAULT_RECEIPT_URL)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)));

        // Check, that the count call also returns 1
        restReceiptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReceiptShouldNotBeFound(String filter) throws Exception {
        restReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReceiptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getReceipt() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        // Get the receipt
        restReceiptMockMvc.perform(get("/api/receipts/{id}", receipt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(receipt.getId().intValue()))
            .andExpect(jsonPath("$.receiptUrl").value(DEFAULT_RECEIPT_URL.toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME.toString()))
            .andExpect(jsonPath("$.fileType").value(DEFAULT_FILE_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingReceipt() throws Exception {
        // Get the receipt
        restReceiptMockMvc.perform(get("/api/receipts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReceipt() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        int databaseSizeBeforeUpdate = receiptRepository.findAll().size();

        // Update the receipt
        Receipt updatedReceipt = receiptRepository.findById(receipt.getId()).get();
        // Disconnect from session so that the updates on updatedReceipt are not directly saved in db
        em.detach(updatedReceipt);
        updatedReceipt
            .receiptUrl(UPDATED_RECEIPT_URL)
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE);
        ReceiptDTO receiptDTO = receiptMapper.toDto(updatedReceipt);

        restReceiptMockMvc.perform(put("/api/receipts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(receiptDTO)))
            .andExpect(status().isOk());

        // Validate the Receipt in the database
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(databaseSizeBeforeUpdate);
        Receipt testReceipt = receiptList.get(receiptList.size() - 1);
        assertThat(testReceipt.getReceiptUrl()).isEqualTo(UPDATED_RECEIPT_URL);
        assertThat(testReceipt.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testReceipt.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingReceipt() throws Exception {
        int databaseSizeBeforeUpdate = receiptRepository.findAll().size();

        // Create the Receipt
        ReceiptDTO receiptDTO = receiptMapper.toDto(receipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReceiptMockMvc.perform(put("/api/receipts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(receiptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Receipt in the database
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteReceipt() throws Exception {
        // Initialize the database
        receiptRepository.saveAndFlush(receipt);

        int databaseSizeBeforeDelete = receiptRepository.findAll().size();

        // Delete the receipt
        restReceiptMockMvc.perform(delete("/api/receipts/{id}", receipt.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Receipt.class);
        Receipt receipt1 = new Receipt();
        receipt1.setId(1L);
        Receipt receipt2 = new Receipt();
        receipt2.setId(receipt1.getId());
        assertThat(receipt1).isEqualTo(receipt2);
        receipt2.setId(2L);
        assertThat(receipt1).isNotEqualTo(receipt2);
        receipt1.setId(null);
        assertThat(receipt1).isNotEqualTo(receipt2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReceiptDTO.class);
        ReceiptDTO receiptDTO1 = new ReceiptDTO();
        receiptDTO1.setId(1L);
        ReceiptDTO receiptDTO2 = new ReceiptDTO();
        assertThat(receiptDTO1).isNotEqualTo(receiptDTO2);
        receiptDTO2.setId(receiptDTO1.getId());
        assertThat(receiptDTO1).isEqualTo(receiptDTO2);
        receiptDTO2.setId(2L);
        assertThat(receiptDTO1).isNotEqualTo(receiptDTO2);
        receiptDTO1.setId(null);
        assertThat(receiptDTO1).isNotEqualTo(receiptDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(receiptMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(receiptMapper.fromId(null)).isNull();
    }
}
