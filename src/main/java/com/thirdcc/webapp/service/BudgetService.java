package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.BudgetDTO;
import com.thirdcc.webapp.service.dto.EventBudgetTotalDTO;
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

    Optional<BudgetDTO> findOneByEventIdAndId(Long eventId, Long id);

    void delete(Long eventId, Long id);

    EventBudgetTotalDTO findTotalEventBudgetByEventId(Long eventId);
}
