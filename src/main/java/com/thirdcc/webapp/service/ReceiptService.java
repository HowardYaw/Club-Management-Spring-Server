package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.ReceiptDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.Receipt}.
 */
public interface ReceiptService {

    /**
     * Save a receipt.
     *
     * @param receiptDTO the entity to save.
     * @return the persisted entity.
     */
    ReceiptDTO save(ReceiptDTO receiptDTO);

    /**
     * Get all the receipts.
     *
     * @return the list of entities.
     */
    List<ReceiptDTO> findAll();


    /**
     * Get the "id" receipt.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReceiptDTO> findOne(Long id);

    /**
     * Delete the "id" receipt.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
