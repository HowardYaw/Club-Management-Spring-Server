package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.ChecklistDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.Checklist}.
 */
public interface ChecklistService {

    /**
     * Save a checklist.
     *
     * @param checklistDTO the entity to save.
     * @return the persisted entity.
     */
    ChecklistDTO save(ChecklistDTO checklistDTO);

    /**
     * Get all the checklists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ChecklistDTO> findAll(Pageable pageable);


    /**
     * Get the "id" checklist.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ChecklistDTO> findOne(Long id);

    /**
     * Delete the "id" checklist.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
