package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.ClubFamilyDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.ClubFamily}.
 */
public interface ClubFamilyService {

    /**
     * Save a clubFamily.
     *
     * @param clubFamilyDTO the entity to save.
     * @return the persisted entity.
     */
    ClubFamilyDTO save(ClubFamilyDTO clubFamilyDTO);

    /**
     * Get all the clubFamilies.
     *
     * @return the list of entities.
     */
    List<ClubFamilyDTO> findAll();


    /**
     * Get the "id" clubFamily.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClubFamilyDTO> findOne(Long id);

    /**
     * Delete the "id" clubFamily.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
