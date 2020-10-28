package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Checklist;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Checklist entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

}
