package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the EventActivity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventActivityRepository extends JpaRepository<EventActivity, Long>, JpaSpecificationExecutor<EventActivity> {

    Page<EventActivity> findAllByEventId(Pageable pageable, Long eventId);
}
