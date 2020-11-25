package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.enumeration.TransactionType;
import com.thirdcc.webapp.service.dto.FinanceReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Map;
import java.util.Optional;

public interface FinanceReportService {

    Page<FinanceReportDTO> findAll(Pageable pageable);

    Optional<FinanceReportDTO> findOneByEventId(Long eventId);

    Map<TransactionType, Map<Month, BigDecimal>> getFinanceReportByYearSession(String yearSession);
}
