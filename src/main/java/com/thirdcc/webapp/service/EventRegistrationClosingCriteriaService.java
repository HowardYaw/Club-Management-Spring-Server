package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.EventRegistrationClosingCriteriaDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.EventRegistrationClosingCriteria}.
 */
public interface EventRegistrationClosingCriteriaService {

    /**
     * Save a eventRegistrationClosingCriteria.
     *
     * @param eventRegistrationClosingCriteriaDTO the entity to save.
     * @return the persisted entity.
     */
    EventRegistrationClosingCriteriaDTO save(EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO);

    /**
     * Get all the eventRegistrationClosingCriteria.
     *
     * @return the list of entities.
     */
    List<EventRegistrationClosingCriteriaDTO> findAll();


    /**
     * Get the "id" eventRegistrationClosingCriteria.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventRegistrationClosingCriteriaDTO> findOne(Long id);

    /**
     * Delete the "id" eventRegistrationClosingCriteria.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
