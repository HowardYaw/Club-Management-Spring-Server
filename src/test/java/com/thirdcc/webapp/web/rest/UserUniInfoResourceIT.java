package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.UserUniInfo;
import com.thirdcc.webapp.repository.UserUniInfoRepository;
import com.thirdcc.webapp.service.UserUniInfoService;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;
import com.thirdcc.webapp.service.mapper.UserUniInfoMapper;
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
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

import static com.thirdcc.webapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.UserUniStatus;
/**
 * Integration tests for the {@Link UserUniInfoResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
public class UserUniInfoResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_FACULTY = "AAAAAAAAAA";
    private static final String UPDATED_FACULTY = "BBBBBBBBBB";

    private static final String DEFAULT_PROGRAM = "AAAAAAAAAA";
    private static final String UPDATED_PROGRAM = "BBBBBBBBBB";

    private static final String DEFAULT_YEAR_SESSION = "AAAAAAAAAA";
    private static final String UPDATED_YEAR_SESSION = "BBBBBBBBBB";

    private static final Integer DEFAULT_INTAKE_SEMESTER = 1;
    private static final Integer UPDATED_INTAKE_SEMESTER = 2;

    private static final BigDecimal DEFAULT_YEAR_OF_STUDY = new BigDecimal(1);
    private static final BigDecimal UPDATED_YEAR_OF_STUDY = new BigDecimal(2);

    private static final String DEFAULT_STAY_IN = "AAAAAAAAAA";
    private static final String UPDATED_STAY_IN = "BBBBBBBBBB";

    private static final UserUniStatus DEFAULT_STATUS = UserUniStatus.GRADUATED;
    private static final UserUniStatus UPDATED_STATUS = UserUniStatus.STUDYING;

    @Autowired
    private UserUniInfoRepository userUniInfoRepository;

    @Autowired
    private UserUniInfoMapper userUniInfoMapper;

    @Autowired
    private UserUniInfoService userUniInfoService;

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

    private MockMvc restUserUniInfoMockMvc;

    private UserUniInfo userUniInfo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserUniInfoResource userUniInfoResource = new UserUniInfoResource(userUniInfoService);
        this.restUserUniInfoMockMvc = MockMvcBuilders.standaloneSetup(userUniInfoResource)
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
    public static UserUniInfo createEntity(EntityManager em) {
        UserUniInfo userUniInfo = new UserUniInfo()
            .userId(DEFAULT_USER_ID)
            .faculty(DEFAULT_FACULTY)
            .program(DEFAULT_PROGRAM)
            .yearSession(DEFAULT_YEAR_SESSION)
            .intakeSemester(DEFAULT_INTAKE_SEMESTER)
            .yearOfStudy(DEFAULT_YEAR_OF_STUDY)
            .stayIn(DEFAULT_STAY_IN)
            .status(DEFAULT_STATUS);
        return userUniInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserUniInfo createUpdatedEntity(EntityManager em) {
        UserUniInfo userUniInfo = new UserUniInfo()
            .userId(UPDATED_USER_ID)
            .faculty(UPDATED_FACULTY)
            .program(UPDATED_PROGRAM)
            .yearSession(UPDATED_YEAR_SESSION)
            .intakeSemester(UPDATED_INTAKE_SEMESTER)
            .yearOfStudy(UPDATED_YEAR_OF_STUDY)
            .stayIn(UPDATED_STAY_IN)
            .status(UPDATED_STATUS);
        return userUniInfo;
    }

    @BeforeEach
    public void initTest() {
        userUniInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserUniInfo() throws Exception {
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
        assertThat(testUserUniInfo.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserUniInfo.getFaculty()).isEqualTo(DEFAULT_FACULTY);
        assertThat(testUserUniInfo.getProgram()).isEqualTo(DEFAULT_PROGRAM);
        assertThat(testUserUniInfo.getYearSession()).isEqualTo(DEFAULT_YEAR_SESSION);
        assertThat(testUserUniInfo.getIntakeSemester()).isEqualTo(DEFAULT_INTAKE_SEMESTER);
        assertThat(testUserUniInfo.getYearOfStudy()).isEqualTo(DEFAULT_YEAR_OF_STUDY);
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
        userUniInfoRepository.saveAndFlush(userUniInfo);

        // Get all the userUniInfoList
        restUserUniInfoMockMvc.perform(get("/api/user-uni-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userUniInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].faculty").value(hasItem(DEFAULT_FACULTY.toString())))
            .andExpect(jsonPath("$.[*].program").value(hasItem(DEFAULT_PROGRAM.toString())))
            .andExpect(jsonPath("$.[*].yearSession").value(hasItem(DEFAULT_YEAR_SESSION.toString())))
            .andExpect(jsonPath("$.[*].intakeSemester").value(hasItem(DEFAULT_INTAKE_SEMESTER)))
            .andExpect(jsonPath("$.[*].yearOfStudy").value(hasItem(DEFAULT_YEAR_OF_STUDY.intValue())))
            .andExpect(jsonPath("$.[*].stayIn").value(hasItem(DEFAULT_STAY_IN.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getUserUniInfo() throws Exception {
        // Initialize the database
        userUniInfoRepository.saveAndFlush(userUniInfo);

        // Get the userUniInfo
        restUserUniInfoMockMvc.perform(get("/api/user-uni-infos/{id}", userUniInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userUniInfo.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.faculty").value(DEFAULT_FACULTY.toString()))
            .andExpect(jsonPath("$.program").value(DEFAULT_PROGRAM.toString()))
            .andExpect(jsonPath("$.yearSession").value(DEFAULT_YEAR_SESSION.toString()))
            .andExpect(jsonPath("$.intakeSemester").value(DEFAULT_INTAKE_SEMESTER))
            .andExpect(jsonPath("$.yearOfStudy").value(DEFAULT_YEAR_OF_STUDY.intValue()))
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
        // Initialize the database
        userUniInfoRepository.saveAndFlush(userUniInfo);

        int databaseSizeBeforeUpdate = userUniInfoRepository.findAll().size();

        // Update the userUniInfo
        UserUniInfo updatedUserUniInfo = userUniInfoRepository.findById(userUniInfo.getId()).get();
        // Disconnect from session so that the updates on updatedUserUniInfo are not directly saved in db
        em.detach(updatedUserUniInfo);
        updatedUserUniInfo
            .userId(UPDATED_USER_ID)
            .faculty(UPDATED_FACULTY)
            .program(UPDATED_PROGRAM)
            .yearSession(UPDATED_YEAR_SESSION)
            .intakeSemester(UPDATED_INTAKE_SEMESTER)
            .yearOfStudy(UPDATED_YEAR_OF_STUDY)
            .stayIn(UPDATED_STAY_IN)
            .status(UPDATED_STATUS);
        UserUniInfoDTO userUniInfoDTO = userUniInfoMapper.toDto(updatedUserUniInfo);

        restUserUniInfoMockMvc.perform(put("/api/user-uni-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isOk());

        // Validate the UserUniInfo in the database
        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(databaseSizeBeforeUpdate);
        UserUniInfo testUserUniInfo = userUniInfoList.get(userUniInfoList.size() - 1);
        assertThat(testUserUniInfo.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserUniInfo.getFaculty()).isEqualTo(UPDATED_FACULTY);
        assertThat(testUserUniInfo.getProgram()).isEqualTo(UPDATED_PROGRAM);
        assertThat(testUserUniInfo.getYearSession()).isEqualTo(UPDATED_YEAR_SESSION);
        assertThat(testUserUniInfo.getIntakeSemester()).isEqualTo(UPDATED_INTAKE_SEMESTER);
        assertThat(testUserUniInfo.getYearOfStudy()).isEqualTo(UPDATED_YEAR_OF_STUDY);
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
        userUniInfoRepository.saveAndFlush(userUniInfo);

        int databaseSizeBeforeDelete = userUniInfoRepository.findAll().size();

        // Delete the userUniInfo
        restUserUniInfoMockMvc.perform(delete("/api/user-uni-infos/{id}", userUniInfo.getId())
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
}
