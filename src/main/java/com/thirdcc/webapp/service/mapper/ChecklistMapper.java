package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.ChecklistDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Checklist} and its DTO {@link ChecklistDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ChecklistMapper extends EntityMapper<ChecklistDTO, Checklist> {



    default Checklist fromId(Long id) {
        if (id == null) {
            return null;
        }
        Checklist checklist = new Checklist();
        checklist.setId(id);
        return checklist;
    }
}
