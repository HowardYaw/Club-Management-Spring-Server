package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.AdministratorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.Administrator}.
 */
public interface AdministratorService {

    /**
     * Save a administrator.
     *
     * @param administratorDTO the entity to save.
     * @return the persisted entity.
     */
    AdministratorDTO save(AdministratorDTO administratorDTO);

    /**
     * Get all the administrators.
     *
     * @return the list of entities.
     */
    List<AdministratorDTO> findAll();


    /**
     * Get the "id" administrator.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AdministratorDTO> findOne(Long id);

    /**
     * Delete the "id" administrator.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get the "userId" administrator list
     *
     * @param userId the userId of the entity
     * @return the list of entities
     */
    List<AdministratorDTO> findAllByUserId(Long userId);

    /**
     * Map Administrator User Details
     * @param administratorDTO the administratorDTO
     * @return the Administrator to map
     */
    AdministratorDTO mapUserDetails(AdministratorDTO administratorDTO);
}
