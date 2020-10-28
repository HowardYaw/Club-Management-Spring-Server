package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.ClaimService;
import com.thirdcc.webapp.domain.Claim;
import com.thirdcc.webapp.repository.ClaimRepository;
import com.thirdcc.webapp.service.dto.ClaimDTO;
import com.thirdcc.webapp.service.mapper.ClaimMapper;
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
     * Get all the claims.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClaimDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Claims");
        return claimRepository.findAll(pageable)
            .map(claimMapper::toDto);
    }


    /**
     * Get one claim by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ClaimDTO> findOne(Long id) {
        log.debug("Request to get Claim : {}", id);
        return claimRepository.findById(id)
            .map(claimMapper::toDto);
    }

    /**
     * Delete the claim by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Claim : {}", id);
        claimRepository.deleteById(id);
    }
}
