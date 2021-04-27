package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.ClubFamilyService;
import com.thirdcc.webapp.service.UserCCInfoService;
import com.thirdcc.webapp.domain.UserCCInfo;
import com.thirdcc.webapp.repository.UserCCInfoRepository;
import com.thirdcc.webapp.service.dto.UserCCInfoDTO;
import com.thirdcc.webapp.service.dto.UserDTO;
import com.thirdcc.webapp.service.mapper.UserCCInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ClubFamilyService clubFamilyService;

    public UserCCInfoServiceImpl(UserCCInfoRepository userCCInfoRepository, UserCCInfoMapper userCCInfoMapper, ClubFamilyService clubFamilyService) {
        this.userCCInfoRepository = userCCInfoRepository;
        this.userCCInfoMapper = userCCInfoMapper;
        this.clubFamilyService = clubFamilyService;
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
