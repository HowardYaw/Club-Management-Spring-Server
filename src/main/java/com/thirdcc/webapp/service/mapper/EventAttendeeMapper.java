package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.EventAttendeeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventAttendee} and its DTO {@link EventAttendeeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventAttendeeMapper extends EntityMapper<EventAttendeeDTO, EventAttendee> {



    default EventAttendee fromId(Long id) {
        if (id == null) {
            return null;
        }
        EventAttendee eventAttendee = new EventAttendee();
        eventAttendee.setId(id);
        return eventAttendee;
    }
}
