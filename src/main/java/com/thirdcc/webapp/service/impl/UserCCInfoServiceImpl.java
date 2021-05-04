package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.enumeration.FishLevel;
import com.thirdcc.webapp.service.ClubFamilyService;
import com.thirdcc.webapp.service.UserCCInfoService;
import com.thirdcc.webapp.domain.UserCCInfo;
import com.thirdcc.webapp.repository.UserCCInfoRepository;
import com.thirdcc.webapp.service.UserUniInfoService;
import com.thirdcc.webapp.service.dto.UserCCInfoDTO;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;
import com.thirdcc.webapp.service.mapper.UserCCInfoMapper;
import com.thirdcc.webapp.utils.YearSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link UserCCInfo}.
 */
@Service
@Transactional
public class UserCCInfoServiceImpl implements UserCCInfoService {

    private final Logger log = LoggerFactory.getLogger(UserCCInfoServiceImpl.class);

    private final UserCCInfoRepository userCCInfoRepository;

    private final UserCCInfoMapper userCCInfoMapper;

    private final ClubFamilyService clubFamilyService;

    private final UserUniInfoService userUniInfoService;

    public UserCCInfoServiceImpl(UserCCInfoRepository userCCInfoRepository,
                                 UserCCInfoMapper userCCInfoMapper,
                                 ClubFamilyService clubFamilyService,
                                 UserUniInfoService userUniInfoService
    ) {
        this.userCCInfoRepository = userCCInfoRepository;
        this.userCCInfoMapper = userCCInfoMapper;
        this.clubFamilyService = clubFamilyService;
        this.userUniInfoService = userUniInfoService;
    }

    /**
     * Save a userCCInfo.
     *
     * @param userCCInfoDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UserCCInfoDTO save(UserCCInfoDTO userCCInfoDTO) {
        log.debug("Request to save UserCCInfo : {}", userCCInfoDTO);
        UserCCInfo userCCInfo = userCCInfoMapper.toEntity(userCCInfoDTO);
        userCCInfo = userCCInfoRepository.save(userCCInfo);
        return userCCInfoMapper.toDto(userCCInfo);
    }

    /**
     * Get all the userCCInfos.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserCCInfoDTO> findAll() {
        log.debug("Request to get all UserCCInfos");
        return userCCInfoRepository.findAll().stream()
            .map(userCCInfoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one userCCInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserCCInfoDTO> findOne(Long id) {
        log.debug("Request to get UserCCInfo : {}", id);
        return userCCInfoRepository.findById(id)
            .map(userCCInfoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserCCInfoDTO> findOneByUserId(Long userId) {
        log.debug("Request to get UserCCInfo by userId : {}", userId);
        return userCCInfoRepository.findByUserId(userId)
            .map(userCCInfoMapper::toDto)
            .map(this::clubFamilyDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCCInfoDTO> getUserCCInfoByUserId(Long userId) {
        log.debug("Request to get UserCCInfo of User : {}", userId);
        List<UserCCInfoDTO> userCCInfoDTOList = userCCInfoRepository
            .findAllByUserId(userId, Pageable.unpaged())
            .map(userCCInfoMapper::toDto)
            .getContent();
        return getFullUserCCInfoList(userCCInfoDTOList, userId);
    }

    /**
     * User CC Fish Level:
     * First Year Session is "Junior Fish"
     * Second Year Session is "Senior Fish"
     * Later Year Session until Present is "Elder Fish"
     *
     * @param userCCInfoDTOList
     * @param userId
     * @return List of user CC Info Sorted by Year Session
     */
    private List<UserCCInfoDTO> getFullUserCCInfoList(List<UserCCInfoDTO> userCCInfoDTOList, Long userId) {
        List<String> yearSessionWithCCFamilyRole = userCCInfoDTOList.stream()
            .map(UserCCInfoDTO::getYearSession)
            .collect(Collectors.toList());
        List<UserCCInfoDTO> fullUserCCInfoDTOList = new ArrayList<>();
        Optional<UserUniInfoDTO> userUniInfoDTOOptional = userUniInfoService.getUserUniInfoByUserId(userId);
        if (userUniInfoDTOOptional.isPresent()) {
            String currentYearSession = YearSessionUtils.getCurrentYearSession();
            String intakeYearSession = userUniInfoDTOOptional.get().getYearSession();
            Integer studySemester = userUniInfoDTOOptional.get().getTotalSemester();
            String yearSessionCounter = intakeYearSession;
            for (int i = 1; i < studySemester; i = i + 2) {
                UserCCInfoDTO userCCInfoDTO = new UserCCInfoDTO();
                if (!yearSessionWithCCFamilyRole.contains(yearSessionCounter)) {
                    userCCInfoDTO.setYearSession(yearSessionCounter);
                    userCCInfoDTO.setUserId(userId);
                }
                else {
                    String currentYearSessionCounter = yearSessionCounter;
                    userCCInfoDTO = userCCInfoDTOList.stream()
                        .filter(userCCInfoDTO1 -> userCCInfoDTO1.getYearSession().equals(currentYearSessionCounter))
                        .findFirst()
                        .get();
                }
                if (i == 1) {
                    userCCInfoDTO.setFishLevel(FishLevel.JUNIOR_FISH);
                } else if (i == 3) {
                    userCCInfoDTO.setFishLevel(FishLevel.SENIOR_FISH);
                } else if (i == 5) {
                    userCCInfoDTO.setFishLevel(FishLevel.ELDER_FISH);
                }
                fullUserCCInfoDTOList.add(userCCInfoDTO);
                if (currentYearSession.compareTo(yearSessionCounter) < 1) {
                    break;
                }
                yearSessionCounter = YearSessionUtils.addYearSessionWithSemester(yearSessionCounter, 2);
            }
            String finalYearSessionCounter = yearSessionCounter;
            userCCInfoDTOList.stream()
                .filter(userCCInfoDTO1 -> userCCInfoDTO1.getYearSession().compareTo(finalYearSessionCounter) > 0)
                .forEach(fullUserCCInfoDTOList::add);
        }
        return fullUserCCInfoDTOList;
    }

    /**
     * Delete the userCCInfo by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserCCInfo : {}", id);
        userCCInfoRepository.deleteById(id);
    }

    private UserCCInfoDTO clubFamilyDetails(UserCCInfoDTO userCCInfoDTO) {
        clubFamilyService.findOne(userCCInfoDTO.getClubFamilyId())
            .ifPresent(clubFamilyDTO -> {
                userCCInfoDTO.setClubFamilyName(clubFamilyDTO.getName());
                userCCInfoDTO.setClubFamilySlogan(clubFamilyDTO.getSlogan());
            });
        return userCCInfoDTO;
    }
}
