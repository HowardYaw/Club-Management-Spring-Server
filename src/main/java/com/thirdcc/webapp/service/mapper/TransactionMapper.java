package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.TransactionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {



    default Transaction fromId(Long id) {
        if (id == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setId(id);
        return transaction;
    }
}
