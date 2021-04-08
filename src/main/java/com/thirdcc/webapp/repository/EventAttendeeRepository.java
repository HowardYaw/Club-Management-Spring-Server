package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventAttendee;
import com.thirdcc.webapp.projections.interfaces.EventAttendeeCustomInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data  repository for the EventAttendee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {

    Page<EventAttendee> findAllByEventId(Pageable pageable, Long eventId);

    Optional<EventAttendee> findOneByEventIdAndUserId(Long eventId , Long userId);

    @Query(value = "SELECT ea.id AS id, ea.user_id AS userId, ea.event_id AS eventId, ea.provide_transport AS provideTransport, u.first_name AS firstName, u.last_name AS lastName, ui.year_session AS yearSession "
            + "FROM "
            + "event_attendee ea JOIN jhi_user u ON u.id = ea.user_id "
            + "JOIN user_uni_info ui ON u.id = ui.user_id "
            + "WHERE ea.event_id = :eventId", nativeQuery = true)
    Page<EventAttendeeCustomInterface> customFindAllByEventId(Pageable pageable, @Param("eventId") Long eventId);
}
