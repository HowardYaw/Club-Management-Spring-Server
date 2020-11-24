package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.FinanceReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FinanceReportService {

    Page<FinanceReportDTO> findAll(Pageable pageable);

    Optional<FinanceReportDTO> findOneByEventId(Long eventId);
}
