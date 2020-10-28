package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.DebtDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Debt} and its DTO {@link DebtDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DebtMapper extends EntityMapper<DebtDTO, Debt> {



    default Debt fromId(Long id) {
        if (id == null) {
            return null;
        }
        Debt debt = new Debt();
        debt.setId(id);
        return debt;
    }
}
