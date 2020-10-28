package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.BudgetDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.Budget}.
 */
public interface BudgetService {

    /**
     * Save a budget.
     *
     * @param budgetDTO the entity to save.
     * @return the persisted entity.
     */
    BudgetDTO save(BudgetDTO budgetDTO);

    /**
     * Get all the budgets.
     *
     * @return the list of entities.
     */
    List<BudgetDTO> findAll();


    /**
     * Get the "id" budget.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BudgetDTO> findOne(Long id);

    /**
     * Delete the "id" budget.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
