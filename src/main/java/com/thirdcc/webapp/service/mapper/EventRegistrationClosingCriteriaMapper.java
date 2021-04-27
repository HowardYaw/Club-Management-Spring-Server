package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.EventRegistrationClosingCriteriaDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventRegistrationClosingCriteria} and its DTO {@link EventRegistrationClosingCriteriaDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventRegistrationClosingCriteriaMapper extends EntityMapper<EventRegistrationClosingCriteriaDTO, EventRegistrationClosingCriteria> {



    default EventRegistrationClosingCriteria fromId(Long id) {
        if (id == null) {
            return null;
        }
        EventRegistrationClosingCriteria eventRegistrationClosingCriteria = new EventRegistrationClosingCriteria();
        eventRegistrationClosingCriteria.setId(id);
        return eventRegistrationClosingCriteria;
    }
}
