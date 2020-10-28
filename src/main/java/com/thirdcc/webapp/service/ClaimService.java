package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.ClaimDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.Claim}.
 */
public interface ClaimService {

    /**
     * Save a claim.
     *
     * @param claimDTO the entity to save.
     * @return the persisted entity.
     */
    ClaimDTO save(ClaimDTO claimDTO);

    /**
     * Get all the claims.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClaimDTO> findAll(Pageable pageable);


    /**
     * Get the "id" claim.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClaimDTO> findOne(Long id);

    /**
     * Delete the "id" claim.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
