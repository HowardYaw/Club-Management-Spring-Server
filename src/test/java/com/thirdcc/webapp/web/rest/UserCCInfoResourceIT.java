package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.UserCCInfo;
import com.thirdcc.webapp.domain.UserUniInfo;
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.domain.enumeration.FishLevel;
import com.thirdcc.webapp.domain.enumeration.UserUniStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.UserCCInfoRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.repository.UserUniInfoRepository;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.UserCCInfoService;
import com.thirdcc.webapp.service.dto.UserCCInfoDTO;
import com.thirdcc.webapp.service.mapper.UserCCInfoMapper;

import com.thirdcc.webapp.utils.YearSessionUtils;
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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.ClubFamilyRole;
/**
 * Integration tests for the {@Link UserCCInfoResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
@InitYearSession
public class UserCCInfoResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_CLUB_FAMILY_ID = 1L;
    private static final Long UPDATED_CLUB_FAMILY_ID = 2L;

    private static final ClubFamilyRole DEFAULT_FAMILY_ROLE = ClubFamilyRole.FATHER;
    private static final ClubFamilyRole UPDATED_FAMILY_ROLE = ClubFamilyRole.MOTHER;

    private static final String DEFAULT_YEAR_SESSION = "2020/2021";
    private static final String UPDATED_YEAR_SESSION = "2019/2020";

    private static final String DEFAULT_UNI_INTAKE_YEAR_SESSION = "2020/2021";
    private static final Long DEFAULT_COURSE_PROGRAM_ID = 1L;
    private static final int DEFAULT_INTAKE_SEMESTER = 1;
    private static final String DEFAULT_STAY_IN = "KK3 UM";
    private static final UserUniStatus DEFAULT_UNI_INFO_STATUS = UserUniStatus.STUDYING;

    private static final FishLevel FIRST_YEAR_FISH_LEVEL = FishLevel.JUNIOR_FISH;
    private static final FishLevel SECOND_YEAR_FISH_LEVEL = FishLevel.SENIOR_FISH;
    private static final FishLevel OLDER_YEAR_FISH_LEVEL = FishLevel.ELDER_FISH;

    @Autowired
    private UserCCInfoRepository userCCInfoRepository;

    @Autowired
    private UserCCInfoMapper userCCInfoMapper;

    @Autowired
    private UserCCInfoService userCCInfoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUniInfoRepository userUniInfoRepository;

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
    public static UserCCInfo createEntity() {
        return new UserCCInfo()
            .userId(DEFAULT_USER_ID)
            .clubFamilyId(DEFAULT_CLUB_FAMILY_ID)
            .familyRole(DEFAULT_FAMILY_ROLE)
            .yearSession(DEFAULT_YEAR_SESSION);
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserCCInfo createUpdatedEntity() {
        return new UserCCInfo()
            .userId(UPDATED_USER_ID)
            .clubFamilyId(UPDATED_CLUB_FAMILY_ID)
            .familyRole(UPDATED_FAMILY_ROLE)
            .yearSession(UPDATED_YEAR_SESSION);
    }

    public static UserUniInfo createUserUniInfoEntity() {
        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(DEFAULT_USER_ID);
        userUniInfo.setYearSession(DEFAULT_UNI_INTAKE_YEAR_SESSION);
        userUniInfo.setCourseProgramId(DEFAULT_COURSE_PROGRAM_ID);
        userUniInfo.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        userUniInfo.setStayIn(DEFAULT_STAY_IN);
        userUniInfo.setStatus(DEFAULT_UNI_INFO_STATUS);
        return userUniInfo;
    }

    @BeforeEach
    public void initTest() {
        userCCInfo = createEntity();
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
    public void getCurrentUserCCInfoProfile_FirstYearWithoutFamilyRole() throws Exception {
        String currentYearSession = YearSessionUtils.getCurrentYearSession();
        User currentUser = getCurrentUser();

        UserUniInfo userUniInfo = createUserUniInfoEntity();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setYearSession(currentYearSession);
        UserUniInfo savedUserUniInfo = userUniInfoRepository.saveAndFlush(userUniInfo);

        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos/current"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(currentUser.getId().intValue())))
            .andExpect(jsonPath("$.[0].fishLevel").value(FIRST_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[0].yearSession").value(currentYearSession))
            .andExpect(jsonPath("$.[0].familyRole").value(nullValue()));
    }

    @Test
    @Transactional
    public void getCurrentUserCCInfoProfile_SecondYearWithoutFamilyRole() throws Exception {
        String currentYearSession = YearSessionUtils.getCurrentYearSession();
        String intakeYearSession = YearSessionUtils.addYearSessionWithSemester(currentYearSession, -2);
        User currentUser = getCurrentUser();

        UserUniInfo userUniInfo = createUserUniInfoEntity();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setYearSession(intakeYearSession);
        UserUniInfo savedUserUniInfo = userUniInfoRepository.saveAndFlush(userUniInfo);

        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos/current"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(currentUser.getId().intValue())))
            .andExpect(jsonPath("$.[0].fishLevel").value(FIRST_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[0].yearSession").value(intakeYearSession))
            .andExpect(jsonPath("$.[0].familyRole").value(nullValue()))
            .andExpect(jsonPath("$.[1].fishLevel").value(SECOND_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[1].yearSession").value(currentYearSession))
            .andExpect(jsonPath("$.[1].familyRole").value(nullValue()));
    }

    @Test
    @Transactional
    public void getCurrentUserCCInfoProfile_SecondYearWithFamilyRole() throws Exception {
        String currentYearSession = YearSessionUtils.getCurrentYearSession();
        String intakeYearSession = YearSessionUtils.addYearSessionWithSemester(currentYearSession, -2);
        User currentUser = getCurrentUser();

        UserUniInfo userUniInfo = createUserUniInfoEntity();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setYearSession(intakeYearSession);
        UserUniInfo savedUserUniInfo = userUniInfoRepository.saveAndFlush(userUniInfo);

        UserCCInfo userCCInfo = createEntity();
        userCCInfo.setUserId(currentUser.getId());
        userCCInfo.setYearSession(currentYearSession);
        UserCCInfo savedUserCCInfo = userCCInfoRepository.saveAndFlush(userCCInfo);

        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos/current"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(currentUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].clubFamilyId").value(hasItem(DEFAULT_CLUB_FAMILY_ID.intValue())))
            .andExpect(jsonPath("$.[0].fishLevel").value(FIRST_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[0].yearSession").value(intakeYearSession))
            .andExpect(jsonPath("$.[0].familyRole").value(nullValue()))
            .andExpect(jsonPath("$.[1].fishLevel").value(SECOND_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[1].yearSession").value(currentYearSession))
            .andExpect(jsonPath("$.[1].familyRole").value(DEFAULT_FAMILY_ROLE.name()));
    }

    @Test
    @Transactional
    public void getCurrentUserCCInfoProfile_ThirdYearWithoutFamilyRole() throws Exception {
        String currentYearSession = YearSessionUtils.getCurrentYearSession();
        String intakeYearSession = YearSessionUtils.addYearSessionWithSemester(currentYearSession, -4);
        String secondYearSession = YearSessionUtils.addYearSessionWithSemester(currentYearSession, -2);
        User currentUser = getCurrentUser();

        UserUniInfo userUniInfo = createUserUniInfoEntity();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setYearSession(intakeYearSession);
        UserUniInfo savedUserUniInfo = userUniInfoRepository.saveAndFlush(userUniInfo);

        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos/current"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(currentUser.getId().intValue())))
            .andExpect(jsonPath("$.[0].fishLevel").value(FIRST_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[0].yearSession").value(intakeYearSession))
            .andExpect(jsonPath("$.[0].familyRole").value(nullValue()))
            .andExpect(jsonPath("$.[1].fishLevel").value(SECOND_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[1].yearSession").value(secondYearSession))
            .andExpect(jsonPath("$.[1].familyRole").value(nullValue()))
            .andExpect(jsonPath("$.[2].fishLevel").value(OLDER_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[2].familyRole").value(nullValue()));
    }

    @Test
    @Transactional
    public void getCurrentUserCCInfoProfile_ThirdYearWithFamilyRole() throws Exception {
        String currentYearSession = YearSessionUtils.getCurrentYearSession();
        String intakeYearSession = YearSessionUtils.addYearSessionWithSemester(currentYearSession, -4);
        String secondYearSession = YearSessionUtils.addYearSessionWithSemester(currentYearSession, -2);
        User currentUser = getCurrentUser();

        UserUniInfo userUniInfo = createUserUniInfoEntity();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setYearSession(intakeYearSession);
        UserUniInfo savedUserUniInfo = userUniInfoRepository.saveAndFlush(userUniInfo);

        UserCCInfo userCCInfo = createEntity();
        userCCInfo.setUserId(currentUser.getId());
        userCCInfo.setYearSession(secondYearSession);
        UserCCInfo savedUserCCInfo = userCCInfoRepository.saveAndFlush(userCCInfo);

        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos/current"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(currentUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].clubFamilyId").value(hasItem(DEFAULT_CLUB_FAMILY_ID.intValue())))
            .andExpect(jsonPath("$.[0].fishLevel").value(FIRST_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[0].yearSession").value(intakeYearSession))
            .andExpect(jsonPath("$.[0].familyRole").value(nullValue()))
            .andExpect(jsonPath("$.[1].fishLevel").value(SECOND_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[1].yearSession").value(secondYearSession))
            .andExpect(jsonPath("$.[1].familyRole").value(DEFAULT_FAMILY_ROLE.name()))
            .andExpect(jsonPath("$.[2].fishLevel").value(OLDER_YEAR_FISH_LEVEL.name()))
            .andExpect(jsonPath("$.[2].familyRole").value(nullValue()));
    }

    @Test
    @Transactional
    public void getCurrentUserCCInfoProfile_WithoutUserUniInfo_ShouldReturnEmptyList() throws Exception {
        User currentUser = getCurrentUser();

        restUserCCInfoMockMvc.perform(get("/api/user-cc-infos/current"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(0)));
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

    private User getCurrentUser() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));
    }
}
