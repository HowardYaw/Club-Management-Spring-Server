package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventAttendee;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Spring Data  repository for the EventAttendee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {

    //overloading the findAllByEventId method
    List<EventAttendee> findAllByEventId(Long eventId);
    
    Page<EventAttendee> findAllByEventId(Long eventId, Pageable pageable);

    Optional<EventAttendee> findOneByEventIdAndUserId(Long eventId , Long userId);
    
}
