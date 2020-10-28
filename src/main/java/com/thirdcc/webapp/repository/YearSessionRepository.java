package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.YearSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the YearSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface YearSessionRepository extends JpaRepository<YearSession, Long> {

}
