package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.EventChecklistDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventChecklist} and its DTO {@link EventChecklistDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventChecklistMapper extends EntityMapper<EventChecklistDTO, EventChecklist> {



    default EventChecklist fromId(Long id) {
        if (id == null) {
            return null;
        }
        EventChecklist checklist = new EventChecklist();
        checklist.setId(id);
        return checklist;
    }
}
