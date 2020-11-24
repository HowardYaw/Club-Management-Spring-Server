package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.FinanceReportService;
import com.thirdcc.webapp.service.dto.FinanceReportDTO;
import com.thirdcc.webapp.service.dto.TransactionDTO;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class FinanceReportResource {
    private final Logger log = LoggerFactory.getLogger(FinanceReportResource.class);

    private static final String ENTITY_NAME = "financeReport";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FinanceReportService financeReportService;


    public FinanceReportResource(FinanceReportService financeReportService) {
        this.financeReportService = financeReportService;
    }

    @GetMapping("/finance-report")
    public ResponseEntity<List<FinanceReportDTO>> getAllEventFinanceReport(Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of FinanceReport");
        Page<FinanceReportDTO> page = financeReportService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/finance-report/{eventId}")
    public ResponseEntity<FinanceReportDTO> getFinanceReportByEventId(@PathVariable Long eventId) {
        log.debug("REST request to get FinanceReport by eventId: {}", eventId);
        Optional<FinanceReportDTO> financeReportDTO = financeReportService.findOneByEventId(eventId);
        return ResponseUtil.wrapOrNotFound(financeReportDTO);
    }


}
