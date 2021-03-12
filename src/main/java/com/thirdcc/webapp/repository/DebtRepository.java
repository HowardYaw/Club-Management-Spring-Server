package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Debt;
import com.thirdcc.webapp.service.dto.DebtDTO;
import java.util.List;
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
    
    Page<Debt> findAllByEventAttendeeIdIn(Pageable pageable, List<Long> eventAttendeeIdList);
}
