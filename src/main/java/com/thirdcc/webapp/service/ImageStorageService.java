package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.ImageStorageDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.ImageStorage}.
 */
public interface ImageStorageService {

    /**
     * Save a imageStorage.
     *
     * @param imageStorageDTO the entity to save.
     * @return the persisted entity.
     */
    ImageStorageDTO save(ImageStorageDTO imageStorageDTO);

    /**
     * Get all the imageStorages.
     *
     * @return the list of entities.
     */
    List<ImageStorageDTO> findAll();


    /**
     * Get the "id" imageStorage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ImageStorageDTO> findOne(Long id);

    /**
     * Delete the "id" imageStorage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
