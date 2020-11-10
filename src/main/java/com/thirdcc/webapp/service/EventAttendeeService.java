package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.EventAttendeeDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.EventAttendee}.
 */
public interface EventAttendeeService {

    /**
     * Save a eventAttendee.
     *
     * @param eventAttendeeventeDTO the entity to save.
     * @return the persisted entity.
     */
    EventAttendeeDTO save(EventAttendeeDTO eventAttendeeDTO);

    /**
     * Get all the eventAttendees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventAttendeeDTO> findAll(Pageable pageable);


    /**
     * Get the "id" eventAttendee.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventAttendeeDTO> findOne(Long id);

    /**
     * Delete the "id" eventAttendee.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
