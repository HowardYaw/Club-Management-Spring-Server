package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.ChecklistService;
import com.thirdcc.webapp.domain.Checklist;
import com.thirdcc.webapp.repository.ChecklistRepository;
import com.thirdcc.webapp.service.dto.ChecklistDTO;
import com.thirdcc.webapp.service.mapper.ChecklistMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Checklist}.
 */
@Service
@Transactional
public class ChecklistServiceImpl implements ChecklistService {

    private final Logger log = LoggerFactory.getLogger(ChecklistServiceImpl.class);

    private final ChecklistRepository checklistRepository;

    private final ChecklistMapper checklistMapper;

    public ChecklistServiceImpl(ChecklistRepository checklistRepository, ChecklistMapper checklistMapper) {
        this.checklistRepository = checklistRepository;
        this.checklistMapper = checklistMapper;
    }

    /**
     * Save a checklist.
     *
     * @param checklistDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ChecklistDTO save(ChecklistDTO checklistDTO) {
        log.debug("Request to save Checklist : {}", checklistDTO);
        Checklist checklist = checklistMapper.toEntity(checklistDTO);
        checklist = checklistRepository.save(checklist);
        return checklistMapper.toDto(checklist);
    }

    /**
     * Get all the checklists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ChecklistDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Checklists");
        return checklistRepository.findAll(pageable)
            .map(checklistMapper::toDto);
    }


    /**
     * Get one checklist by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ChecklistDTO> findOne(Long id) {
        log.debug("Request to get Checklist : {}", id);
        return checklistRepository.findById(id)
            .map(checklistMapper::toDto);
    }

    /**
     * Delete the checklist by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Checklist : {}", id);
        checklistRepository.deleteById(id);
    }
}
