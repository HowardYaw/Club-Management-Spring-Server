package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.YearSession;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link YearSession}.
 */
public interface YearSessionService {

    /**
     * Save a yearSession.
     *
     * @param yearSession the entity to save.
     * @return the persisted entity.
     */
    YearSession save(YearSession yearSession);

    /**
     * Get all the yearSessions.
     *
     * @return the list of entities.
     */
    List<YearSession> findAll();


    /**
     * Get the "id" yearSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<YearSession> findOne(Long id);

    String getDefaultYearSessionString();

    String getYearSessionStringById(Long id);

    /**
     * Delete the "id" yearSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
