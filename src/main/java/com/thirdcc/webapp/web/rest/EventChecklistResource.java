package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.domain.EventChecklist;
import com.thirdcc.webapp.domain.enumeration.EventChecklistStatus;
import com.thirdcc.webapp.service.EventChecklistService;
import com.thirdcc.webapp.service.dto.EventChecklistDTO;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
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

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link EventChecklist}.
 */
@RestController
@RequestMapping("/api")
public class EventChecklistResource {

    private final Logger log = LoggerFactory.getLogger(EventChecklistResource.class);

    private static final String ENTITY_NAME = "checklist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventChecklistService checklistService;

    public EventChecklistResource(EventChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    /**
     * {@code POST  /event-checklists} : Create a new checklist.
     *
     * @param checklistDTO the checklistDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new checklistDTO, or with status {@code 400 (Bad Request)} if the checklist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-checklists")
    public ResponseEntity<EventChecklistDTO> createChecklist(@RequestBody EventChecklistDTO checklistDTO) throws URISyntaxException {
        log.debug("REST request to save Checklist : {}", checklistDTO);
        if (checklistDTO.getId() != null) {
            throw new BadRequestAlertException("A new checklist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EventChecklistDTO result = checklistService.save(checklistDTO);
        return ResponseEntity.created(new URI("/api/checklists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-checklists} : Updates an existing checklist.
     *
     * @param checklistDTO the checklistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checklistDTO,
     * or with status {@code 400 (Bad Request)} if the checklistDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the checklistDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-checklists")
    public ResponseEntity<EventChecklistDTO> updateChecklist(@RequestBody EventChecklistDTO checklistDTO) throws URISyntaxException {
        log.debug("REST request to update Checklist : {}", checklistDTO);
        if (checklistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EventChecklistDTO result = checklistService.update(checklistDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, checklistDTO.getId().toString()))
            .body(result);
    }

    @PutMapping("/event-checklists/{id}/status/{eventChecklistStatus}")
    public ResponseEntity<EventChecklistDTO> updateEventChecklistStatus(@PathVariable Long id, @PathVariable EventChecklistStatus eventChecklistStatus) {
        log.debug("REST request to update event Checklist: {} with status: {}", id, eventChecklistStatus);
        EventChecklistDTO result = checklistService.updateStatus(id, eventChecklistStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * {@code GET  /event-checklists} : get all the checklists.
     *
     * @param pageable the pagination information.
     * @param queryParams a {@link MultiValueMap} query parameters.
     * @param uriBuilder a {@link UriComponentsBuilder} URI builder.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of checklists in body.
     */
    @GetMapping("/event-checklists")
    public ResponseEntity<List<EventChecklistDTO>> getAllChecklists(Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of Checklists");
        Page<EventChecklistDTO> page = checklistService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/event-checklists/event/{eventId}")
    public ResponseEntity<List<EventChecklistDTO>> getAllEventChecklistsByEventId(@PathVariable long eventId, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of Event Checklists with eventId: {}", eventId);
        Page<EventChecklistDTO> page = checklistService.findAllByEventId(eventId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /checklists/:id} : get the "id" checklist.
     *
     * @param id the id of the checklistDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the checklistDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-checklists/{id}")
    public ResponseEntity<EventChecklistDTO> getChecklist(@PathVariable Long id) {
        log.debug("REST request to get Checklist : {}", id);
        Optional<EventChecklistDTO> checklistDTO = checklistService.findOne(id);
        return ResponseUtil.wrapOrNotFound(checklistDTO);
    }

    /**
     * {@code DELETE  /checklists/:id} : delete the "id" checklist.
     *
     * @param id the id of the checklistDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-checklists/{id}")
    public ResponseEntity<Void> deleteChecklist(@PathVariable Long id) {
        log.debug("REST request to delete Checklist : {}", id);
        checklistService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
