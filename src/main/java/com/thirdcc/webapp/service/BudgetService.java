package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.BudgetDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.Budget}.
 */
public interface BudgetService {

    BudgetDTO save(BudgetDTO budgetDTO);

    BudgetDTO update(BudgetDTO budgetDTO);

    List<BudgetDTO> findAllByEventId(Pageable pageable, Long eventId);

    Optional<BudgetDTO> findOne(Long id);

    void delete(Long id);
}
