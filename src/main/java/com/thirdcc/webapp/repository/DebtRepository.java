package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Debt;
import com.thirdcc.webapp.domain.enumeration.DebtStatus;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Debt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    
    Page<Debt> findAllByStatusInOrderByEventAttendeeIdAscCreatedDateAsc(Pageable pageable, Set<DebtStatus> debtStatus);
}
