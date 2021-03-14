package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Debt;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.repository.DebtRepository;
import com.thirdcc.webapp.service.dto.DebtDTO;
import com.thirdcc.webapp.service.mapper.DebtMapper;

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

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.DebtStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.YearSessionRepository;
import com.thirdcc.webapp.service.UserService;
import org.junit.jupiter.api.AfterEach;
/**
 * Integration tests for the {@Link DebtResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = "ADMIN")
public class DebtResourceIT {

    private static final Long DEFAULT_RECEIPT_ID = 1L;
    private static final Long UPDATED_RECEIPT_ID = 2L;

    private static final Long DEFAULT_EVENT_ATTENDEE_ID = 1L;
    private static final Long UPDATED_EVENT_ATTENDEE_ID = 2L;

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(1, 2);
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.valueOf(2, 2);

    private static final DebtStatus DEFAULT_STATUS = DebtStatus.OPEN;
    private static final DebtStatus UPDATED_STATUS = DebtStatus.COLLECTED;
    
    private static final String DEFAULT_YEAR_SESSION_VALUE = "2021/2022";
    
    @Autowired
    private DebtRepository debtRepository;
    
    @Autowired
    private DebtMapper debtMapper;

    @Autowired
    private MockMvc restDebtMockMvc;

    private Debt debt;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private YearSessionRepository yearSessionRepository;

    private User currentUser;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Create an entity for this test.This is a static method, as tests for other entities might also need it,
 if they test an entity which requires the current entity.
     *
     * @return debt with default value
     */
    public static Debt createDebtEntity() {
        return new Debt()
            .receiptId(DEFAULT_RECEIPT_ID)
            .eventAttendeeId(DEFAULT_EVENT_ATTENDEE_ID)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS);
    }
    
    /**
     * Create an updated entity for this test.This is a static method, as tests for other entities might also need it,
 if they test an entity which requires the current entity.
     *
     * @return debt with updated value
     */
    public static Debt createUpdatedDebtEntity() {
        return new Debt()
            .receiptId(UPDATED_RECEIPT_ID)
            .eventAttendeeId(UPDATED_EVENT_ATTENDEE_ID)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);
    }
    
    private YearSession createYearSession() {
        return new YearSession()
            .value(DEFAULT_YEAR_SESSION_VALUE);
    }
    
    @BeforeEach
    public void initTest() {
        debt = createDebtEntity();
        initYearSessionDB();
    }

    @AfterEach
    public void cleanUp() {
        debtRepository.deleteAll();
        yearSessionRepository.deleteAll();
    }
    
    private Debt initDebtDB() {
        return debtRepository.saveAndFlush(debt);
    }
    
    private YearSession initYearSessionDB() {
        return yearSessionRepository.saveAndFlush(new YearSession()
            .value(DEFAULT_YEAR_SESSION_VALUE));
    }
    
    @Test
    public void getAllOpenDebts_UserWithRoleAdmin() throws Exception {
        // Initialize the database
        Debt savedDebt = initDebtDB();

        // Get all the debt in the event
        restDebtMockMvc.perform(get("/api/debts/?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedDebt.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptId").value(hasItem(DEFAULT_RECEIPT_ID.intValue())))
            .andExpect(jsonPath("$.[*].eventAttendeeId").value(hasItem(DEFAULT_EVENT_ATTENDEE_ID.intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    public void getAllOpenDebts_UserWithRoleAdmin_NoDebtWithOpenStatus() throws Exception {
        // Initialize the database
        debt.setStatus(UPDATED_STATUS);
        initDebtDB();

        // Get all the debt in the event
        restDebtMockMvc.perform(get("/api/debts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isEmpty());
    }
    
    @Test
    @WithMockUser
    public void getAllOpenDebts_UserIsNotAdmin_ShouldThrow403IsForbidden() throws Exception {
        // Initialize the database
        currentUser = getLoggedInUser();
        initDebtDB();
        
        // Get all the debt in the event
        restDebtMockMvc.perform(get("/api/debts?sort=id,desc"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void updateDebtStatus_UserWithRoleAdmin() throws Exception {
        // Initialize the database
        initDebtDB();

        int databaseSizeBeforeUpdate = debtRepository.findAll().size();

        restDebtMockMvc.perform(put("/api/debts/{id}/status/{debtStatus}", debt.getId(), UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the Checklist in the database
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeUpdate);
        Debt testDebt = debtList.get(debtList.size() - 1);
        assertThat(testDebt.getReceiptId()).isEqualTo(DEFAULT_RECEIPT_ID);
        assertThat(testDebt.getEventAttendeeId()).isEqualTo(DEFAULT_EVENT_ATTENDEE_ID);
        assertThat(testDebt.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testDebt.getStatus()).isEqualTo(UPDATED_STATUS);
    }
    
    @Test
    @WithMockUser
    public void updateDebtStatus_UserIsNotAdmin_ShouldThrow403IsForbidden() throws Exception {
        // Initialize the database
        initDebtDB();
        currentUser = getLoggedInUser();

        int databaseSizeBeforeUpdate = debtRepository.findAll().size();

        restDebtMockMvc.perform(put("/api/debts/{id}/status/{debtStatus}", debt.getId(), UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        // Validate the Checklist in the database
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeUpdate);
        Debt testDebt = debtList.get(debtList.size() - 1);
        assertThat(testDebt.getReceiptId()).isEqualTo(DEFAULT_RECEIPT_ID);
        assertThat(testDebt.getEventAttendeeId()).isEqualTo(DEFAULT_EVENT_ATTENDEE_ID);
        assertThat(testDebt.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testDebt.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    public void updateDebtStatus_DebtIsNotExists_ShouldThrow400BadRequest() throws Exception {
        // Initialize the database
        initDebtDB();

        int databaseSizeBeforeUpdate = debtRepository.findAll().size();

        restDebtMockMvc.perform(put("/api/debts/{id}/status/{debtStatus}", Long.MAX_VALUE, UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void updateDebtStatus_DebtIsNotOpen_ShouldThrow400BadRequest() throws Exception {
        // Initialize the database
        debt.setStatus(UPDATED_STATUS);
        initDebtDB();

        int databaseSizeBeforeUpdate = debtRepository.findAll().size();

        restDebtMockMvc.perform(put("/api/debts/{id}/status/{debtStatus}", debt.getId(), UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Debt.class);
        Debt debt1 = new Debt();
        debt1.setId(1L);
        Debt debt2 = new Debt();
        debt2.setId(debt1.getId());
        assertThat(debt1).isEqualTo(debt2);
        debt2.setId(2L);
        assertThat(debt1).isNotEqualTo(debt2);
        debt1.setId(null);
        assertThat(debt1).isNotEqualTo(debt2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DebtDTO.class);
        DebtDTO debtDTO1 = new DebtDTO();
        debtDTO1.setId(1L);
        DebtDTO debtDTO2 = new DebtDTO();
        assertThat(debtDTO1).isNotEqualTo(debtDTO2);
        debtDTO2.setId(debtDTO1.getId());
        assertThat(debtDTO1).isEqualTo(debtDTO2);
        debtDTO2.setId(2L);
        assertThat(debtDTO1).isNotEqualTo(debtDTO2);
        debtDTO1.setId(null);
        assertThat(debtDTO1).isNotEqualTo(debtDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(debtMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(debtMapper.fromId(null)).isNull();
    }
    
    private User getLoggedInUser() {
        return userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestException("User not login"));
    }
}
