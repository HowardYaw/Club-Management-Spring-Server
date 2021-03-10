package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.UserCCInfo;
import com.thirdcc.webapp.repository.UserCCInfoRepository;
import com.thirdcc.webapp.service.UserCCInfoService;
import com.thirdcc.webapp.service.dto.UserCCInfoDTO;
import com.thirdcc.webapp.service.mapper.UserCCInfoMapper;

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

import com.thirdcc.webapp.domain.enumeration.ClubFamilyRole;
/**
 * Integration tests for the {@Link UserCCInfoResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class UserCCInfoResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_CLUB_FAMILY_ID = 1L;
    private static final Long UPDATED_CLUB_FAMILY_ID = 2L;

    private static final ClubFamilyRole DEFAULT_FAMILY_ROLE = ClubFamilyRole.FATHER;
    private static final ClubFamilyRole UPDATED_FAMILY_ROLE = ClubFamilyRole.MOTHER;

    private static final String DEFAULT_YEAR_SESSION = "AAAAAAAAAA";
    private static final String UPDATED_YEAR_SESSION = "BBBBBBBBBB";

    @Autowired
    private UserCCInfoRepository userCCInfoRepository;

    @Autowired
    private UserCCInfoMapper userCCInfoMapper;

    @Autowired
    private UserCCInfoService userCCInfoService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserCCInfoMockMvc;

    private UserCCInfo userCCInfo;

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
    public static UserCCInfo createEntity(EntityManager em) {
        UserCCInfo userCCInfo = new UserCCInfo()
            .userId(DEFAULT_USER_ID)
            .clubFamilyId(DEFAULT_CLUB_FAMILY_ID)
            .familyRole(DEFAULT_FAMILY_ROLE)
            .yearSession(DEFAULT_YEAR_SESSION);
        return userCCInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserCCInfo createUpdatedEntity(EntityManager em) {
        UserCCInfo userCCInfo = new UserCCInfo()
            .userId(UPDATED_USER_ID)
            .clubFamilyId(UPDATED_CLUB_FAMILY_ID)
            .familyRole(UPDATED_FAMILY_ROLE)
            .yearSession(UPDATED_YEAR_SESSION);
        return userCCInfo;
    }

    @BeforeEach
    public void initTest() {
        userCCInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserCCInfo() throws Exception {
        int databaseSizeBeforeCreate = userCCInfoRepository.findAll().size();

        // Create the UserCCInfo
        UserCCInfoDTO userCCInfoDTO = userCCInfoMapper.toDto(userCCInfo);
        restUserCCInfoMockMvc.perform(post("/api/user-cc-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userCCInfoDTO)))
            .andExpect(status().isCreated());

        // Validate the UserCCInfo in the database
        List<UserCCInfo> userCCInfoList = userCCInfoRepository.findAll();
        assertThat(userCCInfoList).hasSize(databaseSizeBeforeCreate + 1);
        UserCCInfo testUserCCInfo = userCCInfoList.get(userCCInfoList.size() - 1);
        assertThat(testUserCCInfo.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserCCInfo.getClubFamilyId()).isEqualTo(DEFAULT_CLUB_FAMILY_ID);
        assertThat(testUserCCInfo.getFamilyRole()).isEqualTo(DEFAULT_FAMILY_ROLE);
        assertThat(testUserCCInfo.getYearSession()).isEqualTo(DEFAULT_YEAR_SESSION);
    }

    @Test
    @Transactional
    public void createUserCCInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userCCInfoRepository.findAll().size();

        // Create the UserCCInfo with an existing ID
        userCCInfo.setId(1L);
        UserCCInfoDTO userCCInfoDTO = userCCInfoMapper.toDto(userCCInfo);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserCCInfoMockMvc.perform(post("/api/user-cc-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userCCInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserCCInfo in the database
        List<UserCCInfo> userCCInfoList = userCCInfoRepository.findAll();
        assertThat(userCCInfoList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllUserCCInfos() throws Exception {
        // Initialize the database
        userCCInfoRepository.saveAndFlush(userCCInfo);

        // Get all the userCCInfoList
        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userCCInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].clubFamilyId").value(hasItem(DEFAULT_CLUB_FAMILY_ID.intValue())))
            .andExpect(jsonPath("$.[*].familyRole").value(hasItem(DEFAULT_FAMILY_ROLE.toString())))
            .andExpect(jsonPath("$.[*].yearSession").value(hasItem(DEFAULT_YEAR_SESSION.toString())));
    }

    @Test
    @Transactional
    public void getUserCCInfo() throws Exception {
        // Initialize the database
        userCCInfoRepository.saveAndFlush(userCCInfo);

        // Get the userCCInfo
        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos/{id}", userCCInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userCCInfo.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.clubFamilyId").value(DEFAULT_CLUB_FAMILY_ID.intValue()))
            .andExpect(jsonPath("$.familyRole").value(DEFAULT_FAMILY_ROLE.toString()))
            .andExpect(jsonPath("$.yearSession").value(DEFAULT_YEAR_SESSION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserCCInfo() throws Exception {
        // Get the userCCInfo
        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserCCInfo() throws Exception {
        // Initialize the database
        userCCInfoRepository.saveAndFlush(userCCInfo);

        int databaseSizeBeforeUpdate = userCCInfoRepository.findAll().size();

        // Update the userCCInfo
        UserCCInfo updatedUserCCInfo = userCCInfoRepository.findById(userCCInfo.getId()).get();
        // Disconnect from session so that the updates on updatedUserCCInfo are not directly saved in db
        em.detach(updatedUserCCInfo);
        updatedUserCCInfo
            .userId(UPDATED_USER_ID)
            .clubFamilyId(UPDATED_CLUB_FAMILY_ID)
            .familyRole(UPDATED_FAMILY_ROLE)
            .yearSession(UPDATED_YEAR_SESSION);
        UserCCInfoDTO userCCInfoDTO = userCCInfoMapper.toDto(updatedUserCCInfo);

        restUserCCInfoMockMvc.perform(put("/api/user-cc-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userCCInfoDTO)))
            .andExpect(status().isOk());

        // Validate the UserCCInfo in the database
        List<UserCCInfo> userCCInfoList = userCCInfoRepository.findAll();
        assertThat(userCCInfoList).hasSize(databaseSizeBeforeUpdate);
        UserCCInfo testUserCCInfo = userCCInfoList.get(userCCInfoList.size() - 1);
        assertThat(testUserCCInfo.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserCCInfo.getClubFamilyId()).isEqualTo(UPDATED_CLUB_FAMILY_ID);
        assertThat(testUserCCInfo.getFamilyRole()).isEqualTo(UPDATED_FAMILY_ROLE);
        assertThat(testUserCCInfo.getYearSession()).isEqualTo(UPDATED_YEAR_SESSION);
    }

    @Test
    @Transactional
    public void updateNonExistingUserCCInfo() throws Exception {
        int databaseSizeBeforeUpdate = userCCInfoRepository.findAll().size();

        // Create the UserCCInfo
        UserCCInfoDTO userCCInfoDTO = userCCInfoMapper.toDto(userCCInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserCCInfoMockMvc.perform(put("/api/user-cc-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userCCInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserCCInfo in the database
        List<UserCCInfo> userCCInfoList = userCCInfoRepository.findAll();
        assertThat(userCCInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUserCCInfo() throws Exception {
        // Initialize the database
        userCCInfoRepository.saveAndFlush(userCCInfo);

        int databaseSizeBeforeDelete = userCCInfoRepository.findAll().size();

        // Delete the userCCInfo
        restUserCCInfoMockMvc.perform(delete("/api/user-cc-infos/{id}", userCCInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserCCInfo> userCCInfoList = userCCInfoRepository.findAll();
        assertThat(userCCInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserCCInfo.class);
        UserCCInfo userCCInfo1 = new UserCCInfo();
        userCCInfo1.setId(1L);
        UserCCInfo userCCInfo2 = new UserCCInfo();
        userCCInfo2.setId(userCCInfo1.getId());
        assertThat(userCCInfo1).isEqualTo(userCCInfo2);
        userCCInfo2.setId(2L);
        assertThat(userCCInfo1).isNotEqualTo(userCCInfo2);
        userCCInfo1.setId(null);
        assertThat(userCCInfo1).isNotEqualTo(userCCInfo2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserCCInfoDTO.class);
        UserCCInfoDTO userCCInfoDTO1 = new UserCCInfoDTO();
        userCCInfoDTO1.setId(1L);
        UserCCInfoDTO userCCInfoDTO2 = new UserCCInfoDTO();
        assertThat(userCCInfoDTO1).isNotEqualTo(userCCInfoDTO2);
        userCCInfoDTO2.setId(userCCInfoDTO1.getId());
        assertThat(userCCInfoDTO1).isEqualTo(userCCInfoDTO2);
        userCCInfoDTO2.setId(2L);
        assertThat(userCCInfoDTO1).isNotEqualTo(userCCInfoDTO2);
        userCCInfoDTO1.setId(null);
        assertThat(userCCInfoDTO1).isNotEqualTo(userCCInfoDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userCCInfoMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userCCInfoMapper.fromId(null)).isNull();
    }
}
