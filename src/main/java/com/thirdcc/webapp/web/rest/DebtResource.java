package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.domain.enumeration.DebtStatus;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import com.thirdcc.webapp.service.DebtService;
import com.thirdcc.webapp.service.dto.DebtDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.Debt}.
 */
@RestController
@RequestMapping("/api")
public class DebtResource {

    private final Logger log = LoggerFactory.getLogger(DebtResource.class);

    private static final String ENTITY_NAME = "debt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DebtService debtService;

    public DebtResource(DebtService debtService) {
        this.debtService = debtService;
    }
    
    /**
     * {@code POST  /debts} : Create a new debt.
     *
     * @param debtDTO the debtDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new debtDTO, or with status {@code 400 (Bad Request)} if the debt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/debts")
    public ResponseEntity<DebtDTO> createDebt(@RequestBody DebtDTO debtDTO) throws URISyntaxException {
        log.debug("REST request to save Debt : {}", debtDTO);
        if (debtDTO.getId() != null) {
            throw new BadRequestAlertException("A new debt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DebtDTO result = debtService.save(debtDTO);
        return ResponseEntity.created(new URI("/api/debts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/debts/{id}/status/{debtStatus}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") || @managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<DebtDTO> updateDebtStatus(@PathVariable Long id, @PathVariable DebtStatus debtStatus) {
        log.debug("REST request to update debt: {} with status: {}", id, debtStatus);
        DebtDTO result = debtService.updateStatus(id, debtStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }
    
    @GetMapping("/debts")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") || @managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<List<DebtDTO>> getAllDebts(Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of Debts");
        Page<DebtDTO> page = debtService.findAllOpenDebts(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
