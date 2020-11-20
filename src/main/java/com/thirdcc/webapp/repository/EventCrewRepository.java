package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventCrew;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the EventCrew entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventCrewRepository extends JpaRepository<EventCrew, Long> {

    Optional<EventCrew> findByUserIdAndAndEventId(Long userId, Long eventId);
}
