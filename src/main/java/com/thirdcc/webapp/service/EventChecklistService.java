package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.EventChecklist;
import com.thirdcc.webapp.domain.enumeration.EventChecklistStatus;
import com.thirdcc.webapp.service.dto.EventChecklistDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link EventChecklist}.
 */
public interface EventChecklistService {

    /**
     * Save a checklist.
     *
     * @param checklistDTO the entity to save.
     * @return the persisted entity.
     */
    EventChecklistDTO save(EventChecklistDTO checklistDTO);

    EventChecklistDTO update(EventChecklistDTO eventChecklistDTO);

    /**
     * Get all the checklists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventChecklistDTO> findAll(Pageable pageable);


    /**
     * Get the "id" checklist.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventChecklistDTO> findOne(Long id);

    /**
     * Delete the "id" checklist.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    EventChecklistDTO updateStatus(Long id, EventChecklistStatus eventChecklistStatus);
}
