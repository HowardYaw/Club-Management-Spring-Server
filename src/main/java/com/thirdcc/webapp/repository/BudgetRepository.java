package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Budget;
import com.thirdcc.webapp.domain.enumeration.TransactionType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Budget entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findAllByEventIdAndType(Long eventId, TransactionType type);

}
