package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.FinanceReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FinanceReportService {

    Page<FinanceReportDTO> findAll(Pageable pageable);
}
