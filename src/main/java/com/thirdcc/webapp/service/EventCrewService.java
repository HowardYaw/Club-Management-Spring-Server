package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.EventCrewDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.EventCrew}.
 */
public interface EventCrewService {

    /**
     * Save a eventCrew.
     *
     * @param eventCrewDTO the entity to save.
     * @return the persisted entity.
     */
    EventCrewDTO save(EventCrewDTO eventCrewDTO);

    /**
     * Get all the eventCrews.
     *
     * @return the list of entities.
     */
    List<EventCrewDTO> findAll();


    /**
     * Get the "id" eventCrew.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventCrewDTO> findOne(Long id);

    /**
     * Delete the "id" eventCrew.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}