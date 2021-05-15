package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventRegistrationClosingCriteria;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the EventRegistrationClosingCriteria entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRegistrationClosingCriteriaRepository
  extends JpaRepository<EventRegistrationClosingCriteria, Long>, JpaSpecificationExecutor<EventRegistrationClosingCriteria> {}
