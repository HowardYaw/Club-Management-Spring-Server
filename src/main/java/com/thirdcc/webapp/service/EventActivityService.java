package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.EventActivityDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.EventActivity}.
 */
public interface EventActivityService {

    /**
     * Save a eventActivity.
     *
     * @param eventActivityDTO the entity to save.
     * @return the persisted entity.
     */
    EventActivityDTO save(EventActivityDTO eventActivityDTO);

    /**
     * Get all the eventActivities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventActivityDTO> findAll(Pageable pageable);


    /**
     * Get the "id" eventActivity.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventActivityDTO> findOne(Long id);

    /**
     * Delete the "id" eventActivity.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
