package com.thirdcc.webapp.annotations.authorization;

import com.thirdcc.webapp.annotations.cleanup.CleanUpEventCrew;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.domain.enumeration.EventCrewRole;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithEventCrew.Factory.class)
@CleanUpEventCrew
public @interface WithEventCrew {

    String firstName() default "";

    String email() default "";

    String imageUrl() default "";

    String[] authorities() default {AuthoritiesConstants.USER};

    class Factory extends AbstractSecurityContextFactoryTemplate<WithEventCrew> {

        private final EventCrewRepository eventCrewRepository;
        private final EventRepository eventRepository;

        public Factory(UserRepository userRepository, EventCrewRepository eventCrewRepository, EventRepository eventRepository) {
            super(userRepository);
            this.eventCrewRepository = eventCrewRepository;
            this.eventRepository = eventRepository;
        }

        @Override
        public Set<String> configureAuthorityNames(WithEventCrew annotation) {
            return new HashSet<>(Arrays.asList(annotation.authorities()));
        }

        @Override
        public String createUserEmail(WithEventCrew annotation) {
            boolean hasEmail = !annotation.email().isEmpty();
            if (hasEmail) {
                return annotation.email();
            }
            return super.createUserEmail(annotation);
        }

        @Override
        public String configureFirstName(WithEventCrew annotation) {
            return annotation.firstName();
        }

        @Override
        public String configureImageUrl(WithEventCrew annotation) {
            return annotation.imageUrl();
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
            eventCrew.setRole(EventCrewRole.MEMBER);
            return eventCrewRepository.saveAndFlush(eventCrew);
        }
    }
}
