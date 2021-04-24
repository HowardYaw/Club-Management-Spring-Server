package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithNormalUser;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.UserUniInfo;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.repository.UserUniInfoRepository;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.UserUniInfoService;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;
import com.thirdcc.webapp.service.mapper.UserUniInfoMapper;

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
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.UserUniStatus;
/**
 * Integration tests for the {@Link UserUniInfoResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class UserUniInfoResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_COURSE_PROGRAM_ID = 1L;
    private static final Long UPDATED_COURSE_PROGRAM_ID = 1L;

    private static final String DEFAULT_PROGRAM = "DEFAULT_PROGRAM";
    private static final String UPDATED_PROGRAM = "UPDATED_PROGRAM";

    private static final String DEFAULT_YEAR_SESSION = "DEFAULT_YEAR_SESSION";
    private static final String UPDATED_YEAR_SESSION = "UPDATED_YEAR_SESSION";

    private static final Integer DEFAULT_INTAKE_SEMESTER = 1;
    private static final Integer UPDATED_INTAKE_SEMESTER = 2;

    private static final BigDecimal DEFAULT_YEAR_OF_STUDY = new BigDecimal(1);
    private static final BigDecimal UPDATED_YEAR_OF_STUDY = new BigDecimal(2);

    private static final String DEFAULT_STAY_IN = "DEFAULT_STAY_IN";
    private static final String UPDATED_STAY_IN = "UPDATED_STAY_IN";

    private static final UserUniStatus DEFAULT_STATUS = UserUniStatus.GRADUATED;
    private static final UserUniStatus UPDATED_STATUS = UserUniStatus.STUDYING;

    @Autowired
    private UserUniInfoRepository userUniInfoRepository;

    @Autowired
    private UserUniInfoMapper userUniInfoMapper;

    @Autowired
    private UserUniInfoService userUniInfoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserUniInfoMockMvc;

    private UserUniInfo userUniInfo;

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
    public static UserUniInfo createEntity(EntityManager em) {
        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(DEFAULT_USER_ID);
        userUniInfo.setCourseProgramId(DEFAULT_COURSE_PROGRAM_ID);
        userUniInfo.setYearSession(DEFAULT_YEAR_SESSION);
        userUniInfo.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        userUniInfo.setStayIn(DEFAULT_STAY_IN);
        userUniInfo.setStatus(DEFAULT_STATUS);
        return userUniInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserUniInfo createUpdatedEntity(EntityManager em) {
        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(UPDATED_USER_ID);
        userUniInfo.setCourseProgramId(UPDATED_COURSE_PROGRAM_ID);
        userUniInfo.setYearSession(UPDATED_YEAR_SESSION);
        userUniInfo.setIntakeSemester(UPDATED_INTAKE_SEMESTER);
        userUniInfo.setStayIn(UPDATED_STAY_IN);
        userUniInfo.setStatus(UPDATED_STATUS);
        return userUniInfo;
    }

    @BeforeEach
    public void initTest() {
        userUniInfo = createEntity(em);
    }

    @Test
    @Transactional
    @WithNormalUser
    public void createUserUniInfo() throws Exception {
        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        int databaseSizeBeforeCreate = userUniInfoRepository.findAll().size();

        // Create the UserUniInfo
        UserUniInfoDTO userUniInfoDTO = userUniInfoMapper.toDto(userUniInfo);
        restUserUniInfoMockMvc.perform(post("/api/user-uni-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isCreated());

        // Validate the UserUniInfo in the database
        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(databaseSizeBeforeCreate + 1);
        UserUniInfo testUserUniInfo = userUniInfoList.get(userUniInfoList.size() - 1);
        assertThat(testUserUniInfo.getUserId()).isEqualTo(currentUser.getId());
        assertThat(testUserUniInfo.getCourseProgramId()).isEqualTo(DEFAULT_COURSE_PROGRAM_ID);
        assertThat(testUserUniInfo.getYearSession()).isEqualTo(DEFAULT_YEAR_SESSION);
        assertThat(testUserUniInfo.getIntakeSemester()).isEqualTo(DEFAULT_INTAKE_SEMESTER);
        assertThat(testUserUniInfo.getStayIn()).isEqualTo(DEFAULT_STAY_IN);
        assertThat(testUserUniInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createUserUniInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userUniInfoRepository.findAll().size();

        // Create the UserUniInfo with an existing ID
        userUniInfo.setId(1L);
        UserUniInfoDTO userUniInfoDTO = userUniInfoMapper.toDto(userUniInfo);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserUniInfoMockMvc.perform(post("/api/user-uni-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserUniInfo in the database
        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllUserUniInfos() throws Exception {
        // Initialize the database
        UserUniInfo savedUserUniInfo = initUserUniInfoDB();
        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        // Get all the userUniInfoList
        restUserUniInfoMockMvc.perform(get("/api/user-uni-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedUserUniInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(currentUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].courseProgramId").value(hasItem(DEFAULT_COURSE_PROGRAM_ID.intValue())))
            .andExpect(jsonPath("$.[*].yearSession").value(hasItem(DEFAULT_YEAR_SESSION.toString())))
            .andExpect(jsonPath("$.[*].intakeSemester").value(hasItem(DEFAULT_INTAKE_SEMESTER)))
            .andExpect(jsonPath("$.[*].stayIn").value(hasItem(DEFAULT_STAY_IN.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getUserUniInfo() throws Exception {
        // Initialize the database
        UserUniInfo savedUserUniInfo = initUserUniInfoDB();
        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        // Get the userUniInfo
        restUserUniInfoMockMvc.perform(get("/api/user-uni-infos/{id}", savedUserUniInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(savedUserUniInfo.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(currentUser.getId().intValue()))
            .andExpect(jsonPath("$.courseProgramId").value(DEFAULT_COURSE_PROGRAM_ID.intValue()))
            .andExpect(jsonPath("$.yearSession").value(DEFAULT_YEAR_SESSION.toString()))
            .andExpect(jsonPath("$.intakeSemester").value(DEFAULT_INTAKE_SEMESTER))
            .andExpect(jsonPath("$.stayIn").value(DEFAULT_STAY_IN.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserUniInfo() throws Exception {
        // Get the userUniInfo
        restUserUniInfoMockMvc.perform(get("/api/user-uni-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserUniInfo() throws Exception {

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        // Initialize the database
        UserUniInfo savedUserUniInfo = initUserUniInfoDB();

        int databaseSizeBeforeUpdate = userUniInfoRepository.findAll().size();

        // Update the userUniInfo
        UserUniInfo updatedUserUniInfo = new UserUniInfo();
        updatedUserUniInfo.setId(savedUserUniInfo.getId());
        updatedUserUniInfo.setUserId(UPDATED_USER_ID);
        updatedUserUniInfo.setCourseProgramId(UPDATED_COURSE_PROGRAM_ID);
        updatedUserUniInfo.setYearSession(UPDATED_YEAR_SESSION);
        updatedUserUniInfo.setIntakeSemester(UPDATED_INTAKE_SEMESTER);
        updatedUserUniInfo.setStayIn(UPDATED_STAY_IN);
        updatedUserUniInfo.setStatus(UPDATED_STATUS);
        UserUniInfoDTO userUniInfoDTO = userUniInfoMapper.toDto(updatedUserUniInfo);

        restUserUniInfoMockMvc.perform(put("/api/user-uni-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isOk());

        // Validate the UserUniInfo in the database
        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(databaseSizeBeforeUpdate);
        UserUniInfo testUserUniInfo = userUniInfoList.get(userUniInfoList.size() - 1);
        assertThat(testUserUniInfo.getUserId()).isEqualTo(currentUser.getId());
        assertThat(testUserUniInfo.getCourseProgramId()).isEqualTo(UPDATED_COURSE_PROGRAM_ID);
        assertThat(testUserUniInfo.getYearSession()).isEqualTo(UPDATED_YEAR_SESSION);
        assertThat(testUserUniInfo.getIntakeSemester()).isEqualTo(UPDATED_INTAKE_SEMESTER);
        assertThat(testUserUniInfo.getStayIn()).isEqualTo(UPDATED_STAY_IN);
        assertThat(testUserUniInfo.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingUserUniInfo() throws Exception {
        int databaseSizeBeforeUpdate = userUniInfoRepository.findAll().size();

        // Create the UserUniInfo
        UserUniInfoDTO userUniInfoDTO = userUniInfoMapper.toDto(userUniInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserUniInfoMockMvc.perform(put("/api/user-uni-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserUniInfo in the database
        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUserUniInfo() throws Exception {
        // Initialize the database
        UserUniInfo savedUserUniInfo = initUserUniInfoDB();

        int databaseSizeBeforeDelete = userUniInfoRepository.findAll().size();

        // Delete the userUniInfo
        restUserUniInfoMockMvc.perform(delete("/api/user-uni-infos/{id}", savedUserUniInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserUniInfo.class);
        UserUniInfo userUniInfo1 = new UserUniInfo();
        userUniInfo1.setId(1L);
        UserUniInfo userUniInfo2 = new UserUniInfo();
        userUniInfo2.setId(userUniInfo1.getId());
        assertThat(userUniInfo1).isEqualTo(userUniInfo2);
        userUniInfo2.setId(2L);
        assertThat(userUniInfo1).isNotEqualTo(userUniInfo2);
        userUniInfo1.setId(null);
        assertThat(userUniInfo1).isNotEqualTo(userUniInfo2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserUniInfoDTO.class);
        UserUniInfoDTO userUniInfoDTO1 = new UserUniInfoDTO();
        userUniInfoDTO1.setId(1L);
        UserUniInfoDTO userUniInfoDTO2 = new UserUniInfoDTO();
        assertThat(userUniInfoDTO1).isNotEqualTo(userUniInfoDTO2);
        userUniInfoDTO2.setId(userUniInfoDTO1.getId());
        assertThat(userUniInfoDTO1).isEqualTo(userUniInfoDTO2);
        userUniInfoDTO2.setId(2L);
        assertThat(userUniInfoDTO1).isNotEqualTo(userUniInfoDTO2);
        userUniInfoDTO1.setId(null);
        assertThat(userUniInfoDTO1).isNotEqualTo(userUniInfoDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userUniInfoMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userUniInfoMapper.fromId(null)).isNull();
    }

    private UserUniInfo initUserUniInfoDB() {
        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));
        UserUniInfo userUniInfo = createEntity(em);
        userUniInfo.setUserId(currentUser.getId());
        return userUniInfoRepository.saveAndFlush(userUniInfo);
    }
}
