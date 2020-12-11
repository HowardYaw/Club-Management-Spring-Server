package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventChecklist;
import com.thirdcc.webapp.service.dto.EventChecklistDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Checklist entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventChecklistRepository extends JpaRepository<EventChecklist, Long> {

    Page<EventChecklist> findAllByEventId(Long eventId, Pageable pageable);
}
