package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.UserUniInfoService;
import com.thirdcc.webapp.domain.UserUniInfo;
import com.thirdcc.webapp.repository.UserUniInfoRepository;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;
import com.thirdcc.webapp.service.mapper.UserUniInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link UserUniInfo}.
 */
@Service
@Transactional
public class UserUniInfoServiceImpl implements UserUniInfoService {

    private final Logger log = LoggerFactory.getLogger(UserUniInfoServiceImpl.class);

    private final UserUniInfoRepository userUniInfoRepository;

    private final UserUniInfoMapper userUniInfoMapper;

    public UserUniInfoServiceImpl(UserUniInfoRepository userUniInfoRepository, UserUniInfoMapper userUniInfoMapper) {
        this.userUniInfoRepository = userUniInfoRepository;
        this.userUniInfoMapper = userUniInfoMapper;
    }

    /**
     * Save a userUniInfo.
     *
     * @param userUniInfoDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UserUniInfoDTO save(UserUniInfoDTO userUniInfoDTO) {
        log.debug("Request to save UserUniInfo : {}", userUniInfoDTO);
        UserUniInfo userUniInfo = userUniInfoMapper.toEntity(userUniInfoDTO);
        userUniInfo = userUniInfoRepository.save(userUniInfo);
        return userUniInfoMapper.toDto(userUniInfo);
    }

    /**
     * Get all the userUniInfos.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserUniInfoDTO> findAll() {
        log.debug("Request to get all UserUniInfos");
        return userUniInfoRepository.findAll().stream()
            .map(userUniInfoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one userUniInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserUniInfoDTO> findOne(Long id) {
        log.debug("Request to get UserUniInfo : {}", id);
        return userUniInfoRepository.findById(id)
            .map(userUniInfoMapper::toDto);
    }

    /**
     * Delete the userUniInfo by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserUniInfo : {}", id);
        userUniInfoRepository.deleteById(id);
    }
}