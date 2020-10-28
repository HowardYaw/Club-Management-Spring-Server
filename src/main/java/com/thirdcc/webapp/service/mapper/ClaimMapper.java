package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.ClaimDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Claim} and its DTO {@link ClaimDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ClaimMapper extends EntityMapper<ClaimDTO, Claim> {



    default Claim fromId(Long id) {
        if (id == null) {
            return null;
        }
        Claim claim = new Claim();
        claim.setId(id);
        return claim;
    }
}
