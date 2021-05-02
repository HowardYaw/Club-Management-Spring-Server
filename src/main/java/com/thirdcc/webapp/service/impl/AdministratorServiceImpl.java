package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.AdministratorService;
import com.thirdcc.webapp.domain.Administrator;
import com.thirdcc.webapp.repository.AdministratorRepository;
import com.thirdcc.webapp.service.dto.AdministratorDTO;
import com.thirdcc.webapp.service.mapper.AdministratorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Administrator}.
 */
@Service
@Transactional
public class AdministratorServiceImpl implements AdministratorService {

    private final Logger log = LoggerFactory.getLogger(AdministratorServiceImpl.class);

    private final AdministratorRepository administratorRepository;

    private final AdministratorMapper administratorMapper;

    public AdministratorServiceImpl(AdministratorRepository administratorRepository, AdministratorMapper administratorMapper) {
        this.administratorRepository = administratorRepository;
        this.administratorMapper = administratorMapper;
    }

    /**
     * Save a administrator.
     *
     * @param administratorDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public AdministratorDTO save(AdministratorDTO administratorDTO) {
        log.debug("Request to save Administrator : {}", administratorDTO);
        Administrator administrator = administratorMapper.toEntity(administratorDTO);
        administrator = administratorRepository.save(administrator);
        return administratorMapper.toDto(administrator);
    }

    /**
     * Get all the administrators.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdministratorDTO> findAll() {
        log.debug("Request to get all Administrators");
        return administratorRepository.findAll().stream()
            .map(administratorMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one administrator by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AdministratorDTO> findOne(Long id) {
        log.debug("Request to get Administrator : {}", id);
        return administratorRepository.findById(id)
            .map(administratorMapper::toDto);
    }

    /**
     * Delete the administrator by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Administrator : {}", id);
        administratorRepository.deleteById(id);
    }

    @Override
    public List<AdministratorDTO> findAllByUserId(Long userId) {
        log.debug("Request to get all Administrators by userId: {}", userId);
        return administratorRepository.findAllByUserId(userId).stream()
            .map(administratorMapper::toDto)
            .collect(Collectors.toList());
    }
}
