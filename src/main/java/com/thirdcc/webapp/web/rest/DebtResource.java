package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.domain.enumeration.DebtStatus;
import com.thirdcc.webapp.service.DebtService;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.DebtDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

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

    @PutMapping("/debts/{id}/status/{debtStatus}")
    public ResponseEntity<DebtDTO> updateDebtStatus(@PathVariable Long id, @PathVariable DebtStatus debtStatus) {
        log.debug("REST request to update debt: {} with status: {}", id, debtStatus);
        DebtDTO result = debtService.updateStatus(id, debtStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }
    
    @GetMapping("/debts/event/{eventId}")
    public ResponseEntity<List<DebtDTO>> getAllDebtsByEventId(Pageable pageable, @PathVariable Long eventId, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of Debts");
        Page<DebtDTO> page = debtService.findAllByEventId(pageable, eventId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
//    /**
//     * {@code POST  /debts} : Create a new debt.
//     *
//     * @param debtDTO the debtDTO to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new debtDTO, or with status {@code 400 (Bad Request)} if the debt has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/debts")
//    public ResponseEntity<DebtDTO> createDebt(@RequestBody DebtDTO debtDTO) throws URISyntaxException {
//        log.debug("REST request to save Debt : {}", debtDTO);
//        if (debtDTO.getId() != null) {
//            throw new BadRequestAlertException("A new debt cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        DebtDTO result = debtService.save(debtDTO);
//        return ResponseEntity.created(new URI("/api/debts/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /debts} : Updates an existing debt.
//     *
//     * @param debtDTO the debtDTO to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated debtDTO,
//     * or with status {@code 400 (Bad Request)} if the debtDTO is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the debtDTO couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/debts")
//    public ResponseEntity<DebtDTO> updateDebt(@RequestBody DebtDTO debtDTO) throws URISyntaxException {
//        log.debug("REST request to update Debt : {}", debtDTO);
//        if (debtDTO.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        DebtDTO result = debtService.update(debtDTO);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, debtDTO.getId().toString()))
//            .body(result);
//    }
    
//    /**
//     * {@code GET  /debts} : get all the debts.
//     *
//     * @param pageable the pagination information.
//     * @param queryParams a {@link MultiValueMap} query parameters.
//     * @param uriBuilder a {@link UriComponentsBuilder} URI builder.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of debts in body.
//     */
//    @GetMapping("/debts")
//    public ResponseEntity<List<DebtDTO>> getAllDebts(Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
//        log.debug("REST request to get a page of Debts");
//        Page<DebtDTO> page = debtService.findAll(pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }

//    /**
//     * {@code GET  /debts/:id} : get the "id" debt.
//     *
//     * @param id the id of the debtDTO to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the debtDTO, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/debts/{id}")
//    public ResponseEntity<DebtDTO> getDebt(@PathVariable Long id) {
//        log.debug("REST request to get Debt : {}", id);
//        Optional<DebtDTO> debtDTO = debtService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(debtDTO);
//    }

//    /**
//     * {@code DELETE  /debts/:id} : delete the "id" debt.
//     *
//     * @param id the id of the debtDTO to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/debts/{id}")
//    public ResponseEntity<Void> deleteDebt(@PathVariable Long id) {
//        log.debug("REST request to delete Debt : {}", id);
//        debtService.delete(id);
//        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
//    }
}
