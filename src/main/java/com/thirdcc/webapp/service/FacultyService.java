package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.Faculty;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Faculty}.
 */
public interface FacultyService {

    /**
     * Save a faculty.
     *
     * @param faculty the entity to save.
     * @return the persisted entity.
     */
    Faculty save(Faculty faculty);

    /**
     * Get all the faculties.
     *
     * @return the list of entities.
     */
    List<Faculty> findAll();


    /**
     * Get the "id" faculty.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Faculty> findOne(Long id);

    /**
     * Delete the "id" faculty.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
