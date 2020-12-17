package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.enumeration.DebtStatus;
import com.thirdcc.webapp.service.dto.DebtDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.Debt}.
 */
public interface DebtService {

//    /**
//     * Save a debt.
//     *
//     * @param debtDTO the entity to save.
//     * @return the persisted entity.
//     */
//    DebtDTO save(DebtDTO debtDTO);
//    
//    /**
//     * Update a debt.
//     *
//     * @param debtDTO the entity to update.
//     * @return the persisted entity.
//     */
//    public DebtDTO update(DebtDTO debtDTO);
//    
//    /**
//     * Get all the debts.
//     *
//     * @param pageable the pagination information.
//     * @return the list of entities.
//     */
//    Page<DebtDTO> findAll(Pageable pageable);
//
//
//    /**
//     * Get the "id" debt.
//     *
//     * @param id the id of the entity.
//     * @return the entity.
//     */
//    Optional<DebtDTO> findOne(Long id);
//
//    /**
//     * Delete the "id" debt.
//     *
//     * @param id the id of the entity.
//     */
//    void delete(Long id);
    
    /**
     * Update the debtStatus of the debt to "debtStatus" for "id" debt
     *
     * @param id the id of the entity
     * @param debtStatus the new debtStatus of the entity
     * @return the entity.
     */
    DebtDTO updateStatus(Long id, DebtStatus debtStatus);
    
    /**
     * Get all the debts which is under "eventId" event.
     *
     * @param pageable the pagination information.
     * @param eventId the event id of the event
     * @return the list of entities.
     */
    Page<DebtDTO> findAllByEventId(Pageable pageable, Long eventId);
}
