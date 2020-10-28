package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.EventCrewDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventCrew} and its DTO {@link EventCrewDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventCrewMapper extends EntityMapper<EventCrewDTO, EventCrew> {



    default EventCrew fromId(Long id) {
        if (id == null) {
            return null;
        }
        EventCrew eventCrew = new EventCrew();
        eventCrew.setId(id);
        return eventCrew;
    }
}
