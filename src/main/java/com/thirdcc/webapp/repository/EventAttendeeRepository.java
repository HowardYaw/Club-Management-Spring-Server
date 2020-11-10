package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventAttendee;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


/**
 * Spring Data  repository for the EventAttendee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {

}
