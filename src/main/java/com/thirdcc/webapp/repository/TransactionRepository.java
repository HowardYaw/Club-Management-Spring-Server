package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Transaction;
import com.thirdcc.webapp.domain.enumeration.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data SQL repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findAllByEventId(Long eventId, Pageable pageable);

    List<Transaction> findAllByEventIdAndTransactionType(Long eventId, TransactionType type);

    List<Transaction> findAllByCreatedDateGreaterThanEqualAndCreatedDateLessThan(Instant inclusiveFrom, Instant exclusiveTo);
}
