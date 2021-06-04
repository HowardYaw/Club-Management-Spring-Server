package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.EventRegistrationClosingCriteriaQueryService;
import com.thirdcc.webapp.service.EventRegistrationClosingCriteriaService;
import com.thirdcc.webapp.service.criteria.EventRegistrationClosingCriteriaCriteria;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.EventRegistrationClosingCriteriaDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
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
 * REST controller for managing {@link com.thirdcc.webapp.domain.EventRegistrationClosingCriteria}.
 */
@RestController
@RequestMapping("/api")
public class EventRegistrationClosingCriteriaResource {

    private final Logger log = LoggerFactory.getLogger(EventRegistrationClosingCriteriaResource.class);

    private static final String ENTITY_NAME = "eventRegistrationClosingCriteria";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventRegistrationClosingCriteriaService eventRegistrationClosingCriteriaService;

    private final EventRegistrationClosingCriteriaQueryService eventRegistrationClosingCriteriaQueryService;

    public EventRegistrationClosingCriteriaResource(EventRegistrationClosingCriteriaService eventRegistrationClosingCriteriaService, EventRegistrationClosingCriteriaQueryService eventRegistrationClosingCriteriaQueryService) {
        this.eventRegistrationClosingCriteriaService = eventRegistrationClosingCriteriaService;
        this.eventRegistrationClosingCriteriaQueryService = eventRegistrationClosingCriteriaQueryService;
    }

    /**
     * {@code POST  /event-registration-closing-criteria} : Create a new eventRegistrationClosingCriteria.
     *
     * @param eventRegistrationClosingCriteriaDTO the eventRegistrationClosingCriteriaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventRegistrationClosingCriteriaDTO, or with status {@code 400 (Bad Request)} if the eventRegistrationClosingCriteria has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-registration-closing-criteria")
    public ResponseEntity<EventRegistrationClosingCriteriaDTO> createEventRegistrationClosingCriteria(@RequestBody EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO) throws URISyntaxException {
        log.debug("REST request to save EventRegistrationClosingCriteria : {}", eventRegistrationClosingCriteriaDTO);
        if (eventRegistrationClosingCriteriaDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventRegistrationClosingCriteria cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EventRegistrationClosingCriteriaDTO result = eventRegistrationClosingCriteriaService.save(eventRegistrationClosingCriteriaDTO);
        return ResponseEntity.created(new URI("/api/event-registration-closing-criteria/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-registration-closing-criteria} : Updates an existing eventRegistrationClosingCriteria.
     *
     * @param eventRegistrationClosingCriteriaDTO the eventRegistrationClosingCriteriaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventRegistrationClosingCriteriaDTO,
     * or with status {@code 400 (Bad Request)} if the eventRegistrationClosingCriteriaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventRegistrationClosingCriteriaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-registration-closing-criteria")
    public ResponseEntity<EventRegistrationClosingCriteriaDTO> updateEventRegistrationClosingCriteria(@RequestBody EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO) throws URISyntaxException {
        log.debug("REST request to update EventRegistrationClosingCriteria : {}", eventRegistrationClosingCriteriaDTO);
        if (eventRegistrationClosingCriteriaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EventRegistrationClosingCriteriaDTO result = eventRegistrationClosingCriteriaService.save(eventRegistrationClosingCriteriaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventRegistrationClosingCriteriaDTO.getId().toString()))
            .body(result);
    }

    @GetMapping("/event-registration-closing-criteria")
    public ResponseEntity<List<EventRegistrationClosingCriteriaDTO>> getAllEventRegistrationClosingCriteria(
        EventRegistrationClosingCriteriaCriteria criteria
    ) {
        log.debug("REST request to get EventRegistrationClosingCriteria by criteria: {}", criteria);
        List<EventRegistrationClosingCriteriaDTO> entityList = eventRegistrationClosingCriteriaQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/event-registration-closing-criteria/count")
    public ResponseEntity<Long> countEventRegistrationClosingCriteria(EventRegistrationClosingCriteriaCriteria criteria) {
        log.debug("REST request to count EventRegistrationClosingCriteria by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventRegistrationClosingCriteriaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /event-registration-closing-criteria/:id} : get the "id" eventRegistrationClosingCriteria.
     *
     * @param id the id of the eventRegistrationClosingCriteriaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventRegistrationClosingCriteriaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-registration-closing-criteria/{id}")
    public ResponseEntity<EventRegistrationClosingCriteriaDTO> getEventRegistrationClosingCriteria(@PathVariable Long id) {
        log.debug("REST request to get EventRegistrationClosingCriteria : {}", id);
        Optional<EventRegistrationClosingCriteriaDTO> eventRegistrationClosingCriteriaDTO = eventRegistrationClosingCriteriaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventRegistrationClosingCriteriaDTO);
    }

    /**
     * {@code DELETE  /event-registration-closing-criteria/:id} : delete the "id" eventRegistrationClosingCriteria.
     *
     * @param id the id of the eventRegistrationClosingCriteriaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-registration-closing-criteria/{id}")
    public ResponseEntity<Void> deleteEventRegistrationClosingCriteria(@PathVariable Long id) {
        log.debug("REST request to delete EventRegistrationClosingCriteria : {}", id);
        eventRegistrationClosingCriteriaService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
