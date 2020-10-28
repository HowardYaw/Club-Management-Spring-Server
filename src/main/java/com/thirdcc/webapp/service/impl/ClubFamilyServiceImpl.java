package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.ClubFamilyService;
import com.thirdcc.webapp.domain.ClubFamily;
import com.thirdcc.webapp.repository.ClubFamilyRepository;
import com.thirdcc.webapp.service.dto.ClubFamilyDTO;
import com.thirdcc.webapp.service.mapper.ClubFamilyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link ClubFamily}.
 */
@Service
@Transactional
public class ClubFamilyServiceImpl implements ClubFamilyService {

    private final Logger log = LoggerFactory.getLogger(ClubFamilyServiceImpl.class);

    private final ClubFamilyRepository clubFamilyRepository;

    private final ClubFamilyMapper clubFamilyMapper;

    public ClubFamilyServiceImpl(ClubFamilyRepository clubFamilyRepository, ClubFamilyMapper clubFamilyMapper) {
        this.clubFamilyRepository = clubFamilyRepository;
        this.clubFamilyMapper = clubFamilyMapper;
    }

    /**
     * Save a clubFamily.
     *
     * @param clubFamilyDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ClubFamilyDTO save(ClubFamilyDTO clubFamilyDTO) {
        log.debug("Request to save ClubFamily : {}", clubFamilyDTO);
        ClubFamily clubFamily = clubFamilyMapper.toEntity(clubFamilyDTO);
        clubFamily = clubFamilyRepository.save(clubFamily);
        return clubFamilyMapper.toDto(clubFamily);
    }

    /**
     * Get all the clubFamilies.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClubFamilyDTO> findAll() {
        log.debug("Request to get all ClubFamilies");
        return clubFamilyRepository.findAll().stream()
            .map(clubFamilyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one clubFamily by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ClubFamilyDTO> findOne(Long id) {
        log.debug("Request to get ClubFamily : {}", id);
        return clubFamilyRepository.findById(id)
            .map(clubFamilyMapper::toDto);
    }

    /**
     * Delete the clubFamily by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ClubFamily : {}", id);
        clubFamilyRepository.deleteById(id);
    }
}
