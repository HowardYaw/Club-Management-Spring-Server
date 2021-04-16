package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.enumeration.ClaimStatus;
import com.thirdcc.webapp.service.dto.ClaimDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Update the claimStatus of the claim to "claimStatus" for "id" claim
     *
     * @param id the id of the entity
     * @param claimStatus the new claimStatus of the entity
     * @return the entity.
     */
    ClaimDTO updateStatus(Long id, ClaimStatus claimStatus);
    
    /**
     * Get all claims with OPEN status.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClaimDTO> findAllOpenClaims(Pageable pageable);
}
