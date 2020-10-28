package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.EventImageDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.EventImage}.
 */
public interface EventImageService {

    /**
     * Save a eventImage.
     *
     * @param eventImageDTO the entity to save.
     * @return the persisted entity.
     */
    EventImageDTO save(EventImageDTO eventImageDTO);

    /**
     * Get all the eventImages.
     *
     * @return the list of entities.
     */
    List<EventImageDTO> findAll();


    /**
     * Get the "id" eventImage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventImageDTO> findOne(Long id);

    /**
     * Delete the "id" eventImage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
