package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.BudgetDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Budget} and its DTO {@link BudgetDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BudgetMapper extends EntityMapper<BudgetDTO, Budget> {



    default Budget fromId(Long id) {
        if (id == null) {
            return null;
        }
        Budget budget = new Budget();
        budget.setId(id);
        return budget;
    }
}
