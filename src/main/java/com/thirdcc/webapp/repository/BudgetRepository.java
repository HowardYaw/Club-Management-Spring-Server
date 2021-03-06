package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Budget;
import com.thirdcc.webapp.domain.enumeration.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Budget entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {

    Optional<Budget> findOneByEventIdAndId(Long eventId, Long id);

    List<Budget> findAllByEventIdAndType(Long eventId, TransactionType type);

    Page<Budget> findAllByEventId(Pageable pageable, Long eventId);
}
