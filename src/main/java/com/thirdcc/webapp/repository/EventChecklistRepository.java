package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventChecklist;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Checklist entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventChecklistRepository extends JpaRepository<EventChecklist, Long> {

}
