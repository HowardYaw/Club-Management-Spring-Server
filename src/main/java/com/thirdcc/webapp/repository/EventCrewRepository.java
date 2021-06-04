package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.domain.enumeration.EventCrewRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the EventCrew entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventCrewRepository extends JpaRepository<EventCrew, Long>, JpaSpecificationExecutor<EventCrew> {

    List<EventCrew> findAllByUserId(Long userId);

    Page<EventCrew> findAllByUserId(Pageable pageable, Long userId);

    Page<EventCrew> findAllByEventId(Pageable pageable, Long eventId);

    List<EventCrew> findAllByUserIdAndRole(Long userId, EventCrewRole role);

    Optional<EventCrew> findByUserIdAndAndEventId(Long userId, Long eventId);

    Optional<EventCrew> findByUserIdAndAndEventIdAndRole(Long id, Long eventId, EventCrewRole role);
}
