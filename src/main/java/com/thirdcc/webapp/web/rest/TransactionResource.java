package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.security.AuthoritiesConstants;
import com.thirdcc.webapp.service.TransactionService;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.TransactionDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.Transaction}.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);

    private static final String ENTITY_NAME = "transaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionService transactionService;

    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * {@code POST  /transactions} : Create a new transaction.
     *
     * @param transactionDTO the transactionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionDTO, or with status {@code 400 (Bad Request)} if the transaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transactions")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") || @managementTeamSecurityExpression.isEventCrew(#transactionDTO.getEventId()) || @managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) throws URISyntaxException {
        log.debug("REST request to save Transaction : {}", transactionDTO);
        if (transactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new transaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionDTO result = transactionService.save(transactionDTO);
        return ResponseEntity.created(new URI("/api/transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transactions} : Updates an existing transaction.
     *
     * @param transactionDTO the transactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionDTO,
     * or with status {@code 400 (Bad Request)} if the transactionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transactions")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") || @managementTeamSecurityExpression.isEventHead(#transactionDTO.getEventId()) || @managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<TransactionDTO> updateTransaction(@RequestBody TransactionDTO transactionDTO) throws URISyntaxException {
        log.debug("REST request to update Transaction: {}", transactionDTO);
        if (transactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TransactionDTO result = transactionService.update(transactionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /transactions} : get all the transactions.
     *
     * @param pageable the pagination information.
     * @param queryParams a {@link MultiValueMap} query parameters.
     * @param uriBuilder a {@link UriComponentsBuilder} URI builder.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactions in body.
     */
    @GetMapping("/transactions")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") || @managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of Transactions");
        Page<TransactionDTO> page = transactionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/transactions/event/{eventId}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") || @managementTeamSecurityExpression.isEventCrew(#eventId) || @managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByEventId(@PathVariable Long eventId, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of Transactions by eventId: {}", eventId);
        Page<TransactionDTO> page = transactionService.findAllByEventId(eventId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
