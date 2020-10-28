package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.ClubFamilyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClubFamily} and its DTO {@link ClubFamilyDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ClubFamilyMapper extends EntityMapper<ClubFamilyDTO, ClubFamily> {



    default ClubFamily fromId(Long id) {
        if (id == null) {
            return null;
        }
        ClubFamily clubFamily = new ClubFamily();
        clubFamily.setId(id);
        return clubFamily;
    }
}
