package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.EventImageDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventImage} and its DTO {@link EventImageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventImageMapper extends EntityMapper<EventImageDTO, EventImage> {



    default EventImage fromId(Long id) {
        if (id == null) {
            return null;
        }
        EventImage eventImage = new EventImage();
        eventImage.setId(id);
        return eventImage;
    }
}
