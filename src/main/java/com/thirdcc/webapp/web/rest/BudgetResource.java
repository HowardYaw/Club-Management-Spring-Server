package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.BudgetQueryService;
import com.thirdcc.webapp.service.BudgetService;
import com.thirdcc.webapp.service.criteria.BudgetCriteria;
import com.thirdcc.webapp.service.dto.EventBudgetTotalDTO;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.BudgetDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.Budget}.
 */
@RestController
@RequestMapping("/api")
public class BudgetResource {

    private final Logger log = LoggerFactory.getLogger(BudgetResource.class);

    private static final String ENTITY_NAME = "budget";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BudgetService budgetService;
    private final BudgetQueryService budgetQueryService;

    public BudgetResource(BudgetService budgetService, BudgetQueryService budgetQueryService) {
        this.budgetService = budgetService;
        this.budgetQueryService = budgetQueryService;
    }

    @PostMapping("/event-budget")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventHead(#budgetDTO.getEventId())")
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody BudgetDTO budgetDTO) throws URISyntaxException {
        log.debug("REST request to save Budget : {}", budgetDTO);
        if (budgetDTO.getId() != null) {
            throw new BadRequestAlertException("A new budget cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BudgetDTO result = budgetService.save(budgetDTO);
        return ResponseEntity.created(new URI("/api/budgets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/event-budget")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventHead(#budgetDTO.getEventId())")
    public ResponseEntity<BudgetDTO> updateBudget(@RequestBody BudgetDTO budgetDTO) throws URISyntaxException {
        log.debug("REST request to update Budget : {}", budgetDTO);
        if (budgetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BudgetDTO result = budgetService.update(budgetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, budgetDTO.getId().toString()))
            .body(result);
    }

    @GetMapping("/budgets")
    public ResponseEntity<List<BudgetDTO>> getAllBudgets(BudgetCriteria criteria) {
        log.debug("REST request to get Budgets by criteria: {}", criteria);
        List<BudgetDTO> entityList = budgetQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/budgets/count")
    public ResponseEntity<Long> countBudgets(BudgetCriteria criteria) {
        log.debug("REST request to count Budgets by criteria: {}", criteria);
        return ResponseEntity.ok().body(budgetQueryService.countByCriteria(criteria));
    }


    @GetMapping("/event-budget/event/{eventId}")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventCrew(#eventId)")
    public ResponseEntity<List<BudgetDTO>> getAllBudgetsByEventId(
        Pageable pageable,
        @PathVariable Long eventId,
        @RequestParam MultiValueMap<String, String> queryParams,
        UriComponentsBuilder uriBuilder
    ) {
        log.debug("REST request to get all Budgets by eventId {}", eventId);
        Page<BudgetDTO> page = budgetService.findAllByEventId(pageable, eventId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/event-budget/{id}/event/{eventId}")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventCrew(#eventId)")
    public ResponseEntity<BudgetDTO> getBudget(@PathVariable Long eventId, @PathVariable Long id) {
        log.debug("REST request to get Budget : {}", id);
        Optional<BudgetDTO> budgetDTO = budgetService.findOneByEventIdAndId(eventId, id);
        return ResponseUtil.wrapOrNotFound(budgetDTO);
    }

    @GetMapping("/event-budget/event/{eventId}/total")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventCrew(#eventId)")
    public ResponseEntity<EventBudgetTotalDTO> getTotalBudgetByEventId(@PathVariable Long eventId) {
        log.debug("REST request to get total event budget amount by event Id: {}", eventId);
        EventBudgetTotalDTO result = budgetService.findTotalEventBudgetByEventId(eventId);
        return ResponseEntity.ok()
            .body(result);
    }

    @DeleteMapping("/event-budget/{id}/event/{eventId}")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventHead(#eventId)")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long eventId, @PathVariable Long id) {
        log.debug("REST request to delete Budget : {}", id);
        budgetService.delete(eventId, id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
