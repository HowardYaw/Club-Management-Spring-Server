package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventAttendee;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;



/**
 * Spring Data  repository for the EventAttendee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {
    Optional<EventAttendee> findOneByEventIdAndUserId(Long eventId , Long userId);
}
