package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.ClaimService;
import com.thirdcc.webapp.domain.Claim;
import com.thirdcc.webapp.domain.enumeration.ClaimStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.ClaimRepository;
import com.thirdcc.webapp.service.dto.ClaimDTO;
import com.thirdcc.webapp.service.mapper.ClaimMapper;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Claim}.
 */
@Service
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final Logger log = LoggerFactory.getLogger(ClaimServiceImpl.class);

    private final ClaimRepository claimRepository;

    private final ClaimMapper claimMapper;

    public ClaimServiceImpl(ClaimRepository claimRepository, ClaimMapper claimMapper) {
        this.claimRepository = claimRepository;
        this.claimMapper = claimMapper;
    }

    /**
     * Save a claim.
     *
     * @param claimDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ClaimDTO save(ClaimDTO claimDTO) {
        log.debug("Request to save Claim : {}", claimDTO);
        Claim claim = claimMapper.toEntity(claimDTO);
        claim = claimRepository.save(claim);
        return claimMapper.toDto(claim);
    }
    
    /**
     * Update the claimStatus of the claim to "claimStatus" for "id" claim
     *
     * @param id the id of the entity
     * @param claimStatus the new claimStatus of the entity
     * @return the entity.
     */
    @Override
    public ClaimDTO updateStatus(Long id, ClaimStatus claimStatus) {
        log.debug("Request to update status of claim: {}, to {}", id, claimStatus);
        Claim claim = claimRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Claim not exists for id: " + id));
        if(!ClaimStatus.OPEN.equals(claim.getStatus())){
            throw new BadRequestException("Claim is not open, not allow to update");
        }
        claim.setStatus(claimStatus);
        return claimMapper.toDto(
            claimRepository.save(claim)
        );
    }
    
    /**
     * Get all claims with OPEN status.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClaimDTO> findAllOpenClaims(Pageable pageable) {
        log.debug("Request to get all Claims");
        HashSet<ClaimStatus> openClaimStatus = new HashSet<ClaimStatus>() {{
            add(ClaimStatus.OPEN);
        }};
        return claimRepository.findAllByStatusIn(pageable, openClaimStatus)
            .map(claimMapper::toDto);
    }
}
