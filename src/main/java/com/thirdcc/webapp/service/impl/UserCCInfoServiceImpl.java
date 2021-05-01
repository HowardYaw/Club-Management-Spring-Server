package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.enumeration.FishLevel;
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

    private final UserUniInfoService userUniInfoService;

    public UserCCInfoServiceImpl(UserCCInfoRepository userCCInfoRepository, UserCCInfoMapper userCCInfoMapper, UserUniInfoService userUniInfoService) {
        this.userCCInfoRepository = userCCInfoRepository;
        this.userCCInfoMapper = userCCInfoMapper;
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
        userUniInfoService.getUserUniInfoByUserId(userId)
            .ifPresent(userUniInfoDTO -> {
                String currentYearSession = YearSessionUtils.getCurrentYearSession();
                String intakeYearSession = userUniInfoDTO.getYearSession();
                String secondYearSession = YearSessionUtils.addYearSessionWithSemester(intakeYearSession, 2);
                if (currentYearSession.compareTo(intakeYearSession) > -1) {
                    if (!yearSessionWithCCFamilyRole.contains(intakeYearSession)) {
                        fullUserCCInfoDTOList.add(constructCustomUserCCInfo(intakeYearSession, FishLevel.JUNIOR_FISH, userId));
                    }
                    else {
                        UserCCInfoDTO userCCInfoDTO = userCCInfoDTOList.stream()
                            .filter(userCCInfoDTO1 -> userCCInfoDTO1.getYearSession().equals(intakeYearSession))
                            .findFirst()
                            .get();
                        userCCInfoDTO.setFishLevel(FishLevel.JUNIOR_FISH);
                        fullUserCCInfoDTOList.add(userCCInfoDTO);
                    }
                }
                if (currentYearSession.compareTo(secondYearSession) > -1) {
                    if (!yearSessionWithCCFamilyRole.contains(secondYearSession)) {
                        fullUserCCInfoDTOList.add(constructCustomUserCCInfo(secondYearSession, FishLevel.SENIOR_FISH, userId));
                    }
                    else {
                        UserCCInfoDTO userCCInfoDTO = userCCInfoDTOList.stream()
                            .filter(userCCInfoDTO1 -> userCCInfoDTO1.getYearSession().equals(secondYearSession))
                            .findFirst()
                            .get();
                        userCCInfoDTO.setFishLevel(FishLevel.SENIOR_FISH);
                        fullUserCCInfoDTOList.add(userCCInfoDTO);
                    }
                }
                if (currentYearSession.compareTo(secondYearSession) > 0) {
                    String thirdYearSession = YearSessionUtils.addYearSessionWithSemester(secondYearSession, 2) + " - Present";
                    fullUserCCInfoDTOList.add(constructCustomUserCCInfo(thirdYearSession, FishLevel.ELDER_FISH, userId));
                }
                userCCInfoDTOList.stream()
                    .filter(userCCInfoDTO1 -> userCCInfoDTO1.getYearSession().compareTo(secondYearSession) > 0)
                    .forEach(fullUserCCInfoDTOList::add);
            });
        fullUserCCInfoDTOList.sort(Comparator.comparing(UserCCInfoDTO::getYearSession));
        return fullUserCCInfoDTOList;
    }

    private UserCCInfoDTO constructCustomUserCCInfo(String yearSession, FishLevel fishLevel, Long userId) {
        UserCCInfoDTO userCCInfoDTO = new UserCCInfoDTO();
        userCCInfoDTO.setFishLevel(fishLevel);
        userCCInfoDTO.setYearSession(yearSession);
        userCCInfoDTO.setUserId(userId);
        return userCCInfoDTO;
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
}
