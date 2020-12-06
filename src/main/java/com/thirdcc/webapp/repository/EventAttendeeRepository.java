package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventAttendee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the EventAttendee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {

    Page<EventAttendee> findAllByEventId(Pageable pageable, Long eventId);

}
