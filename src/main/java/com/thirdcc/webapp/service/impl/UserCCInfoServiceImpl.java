package com.thirdcc.webapp.service.impl;

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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
    public List<UserCCInfoDTO> getUserCCInfoByUserId(Long userId) {
        log.debug("Request to get UserCCInfo of User : {}", userId);
        List<UserCCInfoDTO> userCCInfoDTOList = userCCInfoRepository
            .findAllByUserId(userId, Pageable.unpaged())
            .map(userCCInfoMapper::toDto)
            .getContent();
        return getFullUserCCInfoList(userCCInfoDTOList, userId);
//        userCCInfoDTOList.sort(Comparator.comparing(UserCCInfoDTO::getYearSession));
//        return userCCInfoDTOList;
    }

    private List<UserCCInfoDTO> getFullUserCCInfoList(List<UserCCInfoDTO> userCCInfoDTOList, Long userId) {
        List<String> yearSessionWithCCFamilyRole = userCCInfoDTOList.stream()
            .map(UserCCInfoDTO::getYearSession)
            .collect(Collectors.toList());
        userUniInfoService.getUserUniInfoByUserId(userId)
            .ifPresent(userUniInfoDTO -> {
                String currentYearSession = YearSessionUtils.getCurrentYearSession();
                // TODO: Construct Full List
            });
        userCCInfoDTOList.sort(Comparator.comparing(UserCCInfoDTO::getYearSession));
        return userCCInfoDTOList;
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
