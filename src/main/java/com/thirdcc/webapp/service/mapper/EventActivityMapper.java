package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.EventActivityDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventActivity} and its DTO {@link EventActivityDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventActivityMapper extends EntityMapper<EventActivityDTO, EventActivity> {



    default EventActivity fromId(Long id) {
        if (id == null) {
            return null;
        }
        EventActivity eventActivity = new EventActivity();
        eventActivity.setId(id);
        return eventActivity;
    }
}
