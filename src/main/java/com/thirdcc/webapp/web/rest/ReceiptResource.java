package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.ReceiptQueryService;
import com.thirdcc.webapp.service.ReceiptService;
import com.thirdcc.webapp.service.criteria.ReceiptCriteria;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.ReceiptDTO;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.Receipt}.
 */
@RestController
@RequestMapping("/api")
public class ReceiptResource {

    private final Logger log = LoggerFactory.getLogger(ReceiptResource.class);

    private static final String ENTITY_NAME = "receipt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReceiptService receiptService;

    private final ReceiptQueryService receiptQueryService;

    public ReceiptResource(ReceiptService receiptService, ReceiptQueryService receiptQueryService) {
        this.receiptService = receiptService;
        this.receiptQueryService = receiptQueryService;
    }

    /**
     * {@code POST  /receipts} : Create a new receipt.
     *
     * @param receiptDTO the receiptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new receiptDTO, or with status {@code 400 (Bad Request)} if the receipt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/receipts")
    public ResponseEntity<ReceiptDTO> createReceipt(@RequestBody ReceiptDTO receiptDTO) throws URISyntaxException {
        log.debug("REST request to save Receipt : {}", receiptDTO);
        if (receiptDTO.getId() != null) {
            throw new BadRequestAlertException("A new receipt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReceiptDTO result = receiptService.save(receiptDTO);
        return ResponseEntity.created(new URI("/api/receipts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /receipts} : Updates an existing receipt.
     *
     * @param receiptDTO the receiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated receiptDTO,
     * or with status {@code 400 (Bad Request)} if the receiptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the receiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/receipts")
    public ResponseEntity<ReceiptDTO> updateReceipt(@RequestBody ReceiptDTO receiptDTO) throws URISyntaxException {
        log.debug("REST request to update Receipt : {}", receiptDTO);
        if (receiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ReceiptDTO result = receiptService.save(receiptDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, receiptDTO.getId().toString()))
            .body(result);
    }

    @GetMapping("/receipts")
    public ResponseEntity<List<ReceiptDTO>> getAllReceipts(ReceiptCriteria criteria) {
        log.debug("REST request to get Receipts by criteria: {}", criteria);
        List<ReceiptDTO> entityList = receiptQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/receipts/count")
    public ResponseEntity<Long> countReceipts(ReceiptCriteria criteria) {
        log.debug("REST request to count Receipts by criteria: {}", criteria);
        return ResponseEntity.ok().body(receiptQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /receipts/:id} : get the "id" receipt.
     *
     * @param id the id of the receiptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the receiptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/receipts/{id}")
    public ResponseEntity<ReceiptDTO> getReceipt(@PathVariable Long id) {
        log.debug("REST request to get Receipt : {}", id);
        Optional<ReceiptDTO> receiptDTO = receiptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(receiptDTO);
    }

    /**
     * {@code DELETE  /receipts/:id} : delete the "id" receipt.
     *
     * @param id the id of the receiptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/receipts/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        log.debug("REST request to delete Receipt : {}", id);
        receiptService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
