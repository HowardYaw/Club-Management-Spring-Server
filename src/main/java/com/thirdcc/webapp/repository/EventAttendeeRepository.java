package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventAttendee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the EventAttendee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long>, JpaSpecificationExecutor<EventAttendee> {

    //overloading the findAllByEventId method
    List<EventAttendee> findAllByEventId(Long eventId);

    Page<EventAttendee> findAllByEventId(Long eventId, Pageable pageable);

    Optional<EventAttendee> findOneByEventIdAndUserId(Long eventId , Long userId);
}
