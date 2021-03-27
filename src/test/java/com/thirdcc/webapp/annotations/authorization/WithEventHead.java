package com.thirdcc.webapp.annotations.authorization;

import com.thirdcc.webapp.annotations.cleanup.CleanUpEventHead;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.domain.enumeration.EventCrewRole;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.repository.*;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithEventHead.Factory.class)
@CleanUpEventHead
public @interface WithEventHead {

    class Factory extends AbstractSecurityContextFactoryTemplate<WithEventHead> {

        private final EventCrewRepository eventCrewRepository;
        private final EventRepository eventRepository;

        public Factory(UserRepository userRepository, EventCrewRepository eventCrewRepository, EventRepository eventRepository) {
            super(userRepository);
            this.eventCrewRepository = eventCrewRepository;
            this.eventRepository = eventRepository;
        }

        @Override
        public Set<String> configureAuthorityNames() {
            return new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER));
        }

        @Override
        public void onSecurityContextCreatedHook() {
            Event savedEvent = initEventDB();
            EventCrew savedEventCrew = initEventCrewDB(savedEvent);
        }

        private Event initEventDB() {
            Event event = new Event();
            event.setName("DEFAULT_EVENT_NAME");
            event.setDescription("DEFAULT_EVENT_DESCRIPTION");
            event.setRemarks("DEFAULT_EVENT_REMARKS");
            event.setVenue("DEFAULT_EVENT_VENUE");
            event.setStartDate(Instant.now().minus(5, ChronoUnit.DAYS));
            event.setEndDate(Instant.now().plus(5, ChronoUnit.DAYS));
            event.setFee(new BigDecimal(2123));
            event.setRequiredTransport(Boolean.TRUE);
            event.setStatus(EventStatus.OPEN);
            return eventRepository.saveAndFlush(event);
        }

        private EventCrew initEventCrewDB(Event event) {
            EventCrew eventCrew = new EventCrew();
            eventCrew.setUserId(getUser().getId());
            eventCrew.setEventId(event.getId());
            eventCrew.setRole(EventCrewRole.HEAD);
            return eventCrewRepository.saveAndFlush(eventCrew);
        }
    }
}
