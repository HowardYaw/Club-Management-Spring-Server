package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Transaction;
import com.thirdcc.webapp.domain.enumeration.TransactionType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByEventIdAndType(Long eventId, TransactionType type);

}
