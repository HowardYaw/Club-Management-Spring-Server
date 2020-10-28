package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.AdministratorDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Administrator} and its DTO {@link AdministratorDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AdministratorMapper extends EntityMapper<AdministratorDTO, Administrator> {



    default Administrator fromId(Long id) {
        if (id == null) {
            return null;
        }
        Administrator administrator = new Administrator();
        administrator.setId(id);
        return administrator;
    }
}
